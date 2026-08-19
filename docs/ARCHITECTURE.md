# ActiveGroup "Zeus" — Memarlıq və Kod Strukturu Sənədi

Bu sənəd layihənin (anbar idarəetmə PDA tətbiqi) hansı məntiqlə, hansı
paketləmə (package) strukturunda yazıldığını izah edir: **repository**,
**service (API)**, **DTO**, **mapleme (mapping)**, **ViewModel** və
**XML UI** qatları.

- **applicationId:** `eryaz.software.activegroup`
- **Dil:** Kotlin
- **Memarlıq:** MVVM + Repository pattern
- **DI:** Koin `3.1.4`
- **Şəbəkə:** Retrofit `2.9.0` + OkHttp + Gson
- **UI:** DataBinding + ViewBinding + Navigation Component (SafeArgs)

---

## 1. Modul Strukturu

Layihə **2 Gradle modulundan** ibarətdir (`settings.gradle`):

```
activegorup
├── :app     → UI qatı (com.android.application)
└── :data    → Data qatı (com.android.library)
```

| Modul | Məsuliyyət |
|-------|-----------|
| **`:app`** | Bütün UI: `Fragment`, `Activity`, `ViewModel`, adapter, dialog, custom widget, binding adapter. `:data`-ya `implementation project(':data')` ilə bağlıdır. |
| **`:data`** | Şəbəkə (Retrofit/OkHttp), repository, DTO, request/response model, mapper, persistence, API+repo üçün DI. Koin/Retrofit/Timber-i `api(...)` kimi expose edir ki, `:app` da miras alsın. |

`BuildConfig` sahələri `gradle.properties`-dən gəlir:

```properties
BASE_URL="http://192.168.22.26:21021/"       # LAN (daxili şəbəkə)
BASE_OUT_URL="http://37.32.65.118:21021"     # xarici (public)
```

---

## 2. Paketləmə (Package) Strukturu

Hər iki modul eyni əsas paketdən istifadə edir: `eryaz.software.activegroup`.

### `:data` moduluı — `data/.../data/`

| Paket | Məzmun |
|-------|--------|
| `api/client` | `ZeusClient.kt` — Retrofit/OkHttp factory |
| `api/interceptors` | OkHttp interceptor-lar (Authorization, UnAuthorized, Logging) |
| `api/services` | Retrofit `interface`-ləri (`*ApiService` / `*Service`) |
| `api/utils` | `Resource`, `ResponseHandler`, `NetworkUtils` |
| `di` | `appModuleApis`, `appModuleRepos` (Koin) |
| `enums` | `UiState`, `Language`, `ResponseStatus` və s. |
| `mappers` | `Response → Dto` extension funksiyaları (`toDto()`) |
| `models/dto` | `*Dto` — domain/UI modelləri (~50 fayl) |
| `models/remote/models` | `ResultModel`, `PdaVersionModel` |
| `models/remote/request` | `*Request` / `*RequestModel` |
| `models/remote/response` | `*Response` — şəbəkə modelləri (~49 fayl) |
| `persistence` | `SessionManager`, cache-lər |
| `repositories` | `*Repo` + `BaseRepo` |

### `:app` modulu — `app/.../activegroup/`

| Paket | Məzmun |
|-------|--------|
| `core` | `ZeusApp` (Application), `ApkDownloadService` |
| `di` | `AppModule.kt` (ViewModel-lər) |
| `ui/base` | `BaseFragment`, `BaseActivity`, `BaseViewModel`, `BaseDialogFragment` |
| `ui/auth` | Login |
| `ui/dashboard` | `inbound/`, `outbound/`, `movement/`, `counting/`, `query/`, `recording/`, `settings/` |
| `util` | `adapter/`, `bindingAdapter/`, `dialogs/`, `extensions/`, `widgets/` |

### Feature-folder qaydası (konvensiya)

Hər ekran öz qovluğunda təxminən 2 fayl saxlayır:
`XxxFragment.kt` + `XxxVM.kt`, altında lazım gələrsə `dialog/`, `adapter/`,
alt-ekran qovluqları. Nümunə:

```
ui/dashboard/outbound/orderPicking/orderPickingDetail/
├── OrderPickingDetailFragment.kt
├── OrderPickingDetailVM.kt
├── dialog/
└── changeQuantity/
```

---

## 3. Repository Qatı

**Yer:** `data/.../data/repositories/`
**Adlandırma:** `XxxRepo` (`AuthRepo`, `UserRepo`, `OrderRepo`, `CountingRepo`, `PlacementRepo`, `BarcodeRepo`, `WorkActivityRepo`).

Bütün repo-lar `BaseRepo`-dan miras alır və API service-i konstruktor ilə alır.
`BaseRepo` hər çağırışı `Dispatchers.IO` + try/catch içində icra edir və
xətaları `ResponseHandler`-ə ötürür:

```9:22:data/src/main/java/eryaz/software/activegroup/data/repositories/BaseRepo.kt
abstract class BaseRepo(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun <T> callApi(
        func: suspend () -> Resource<T>
    ): Resource<T> {
        return withContext(dispatcher) {
            try {
                func.invoke()
            } catch (e: Exception) {
                ResponseHandler.handleException(e)
            }
        }
    }
}
```

Standart repo metodunun forması: `callApi { api.call(); ResponseHandler.handleSuccess(response, response.result.toDto()) }`.

```8:19:data/src/main/java/eryaz/software/activegroup/data/repositories/AuthRepo.kt
class AuthRepo(private val api: AuthApiService) : BaseRepo() {

    suspend fun login(request: LoginRequest) = callApi {
        val response = api.login(request)
        ResponseHandler.handleSuccess(response, response.result)
    }

    suspend fun getPdaVersion() = callApi {
        val response = api.getPdaVersion()
        ResponseHandler.handleSuccess(response, response.result.toDto())
    }
}
```

`handleSuccess`-ə ötürülən dataya görə 3 variant olur:
- `response.result` — xam nəticə (mapleme yoxdur),
- `response.result.map { it.toDto() }` — siyahı DTO-lara maplenir,
- `response.success` — sadəcə `Boolean`.

> **Vacib qayda:** `.toDto()` mapleme YALNIZ repository içində tətbiq olunur.
> Bu, DTO sərhədini (boundary) təşkil edir — UI qatı yalnız DTO görür.

---

## 4. Service / API Qatı

**Yer:** `data/.../data/api/services/`
**Adlandırma:** qarışıq — `AuthApiService`, `UserApiService`, həmçinin `WorkActivityService`, `BarcodeService`, `PlacementService`, `OrderService`, `CountingService`.

Sadə Retrofit `interface`-lərdir. Bütün metodlar `suspend`-dir və
`ResultModel<T>` (response wrapper) qaytarır — `Resource` yox.

```11:18:data/src/main/java/eryaz/software/activegroup/data/api/services/AuthApiService.kt
interface AuthApiService {

    @POST("api/TokenAuth/Authenticate")
    suspend fun login(@Body request: LoginRequest?): ResultModel<UserResponse>

    @GET("api/services/app/Common/GetPdaVersion")
    suspend fun getPdaVersion(): ResultModel<PdaVersionModel>
}
```

Konvensiyalar:
- ASP.NET stili path-lar: `api/services/app/<Controller>/<Action>`
- GET parametrləri: `@Query("companyId")`
- POST body: `@Body`
- Generic-lər: `ResultModel<List<CompanyResponse>>`, `ResultModel<Boolean>`

---

## 5. DTO Qatı

**Yer:** `data/.../data/models/dto/` (~50 fayl)
**Adlandırma:** `XxxDto`.

Demək olar həmişə `@Parcelize data class ... : Parcelable`
(Navigation SafeArgs və Koin parametrli VM-lərdən keçə bilsin deyə).
Sahələr artıq UI-formasındadır (məs. `quantity: String`, hesablanmış `fullName`).

```6:11:data/src/main/java/eryaz/software/activegroup/data/models/dto/CompanyDto.kt
@Parcelize
data class CompanyDto(
    val code: String,
    val definition: String,
    val id: Int
):Parcelable
```

DTO-lar bəzən yalnız UI üçün olan sahələr də daşıyır (məs. `showAnimation: Boolean = false`).

---

## 6. Mapleme (Response → DTO Mapping)

**Yer:** `data/.../data/mappers/` (~13 fayl: `UserMapper.kt`, `CompanyMapper.kt`, `ClientMapper.kt`, `ShelfMapper.kt` və s.)

**Pattern:** `*Response` üzərində təyin olunmuş, hamısı `toDto()` adlı
**top-level extension funksiya**. Mapper class/interface YOXDUR — saf Kotlin
extension-larıdır, receiver tipinə görə resolve olunur. İç-içə obyektlər
`?.toDto()` zənciri ilə maplenir.

```12:22:data/src/main/java/eryaz/software/activegroup/data/mappers/CompanyMapper.kt
fun CompanyResponse.toDto() = CompanyDto(
    code = code,
    definition = definition,
    id = id
)

fun WarehouseResponse.toDto() = WarehouseDto(
    code = code.orEmpty(),
    name = name.orEmpty(),
    id = id
)
```

Mapleme null-safe-dir (`.orEmpty()`), sahələri birləşdirə bilir
(məs. `fullName = "$name $surname"`) və yalnız repository içindən çağırılır.

---

## 7. ViewModel Qatı

**Yer:** hər feature-in öz qovluğunda (`:app`).
**Adlandırma:** əsasən `XxxVM`, bəziləri `XxxViewModel`.

Bütün VM-lər `BaseViewModel`-dən miras alır və repo-nu konstruktor (Koin) ilə alır.

`BaseViewModel` ortaq dialog/UI-state `StateFlow`-larını və mərkəzi
`executeInBackground` helper-ini saxlayır — o, suspend `Resource<T>` producer-i
işlədir, `UiState`-ə çevirir və avtomatik error/progress dialoqları göstərir:

```17:66:app/src/main/java/eryaz/software/activegroup/ui/base/BaseViewModel.kt
open class BaseViewModel : ViewModel() {
    private val _showErrorDialog = MutableStateFlow<ErrorDialogDto?>(null)
    val showErrorDialog = _showErrorDialog.asStateFlow()
    ...
    protected val _uiState = MutableStateFlow(UiState.LOADING)
    val uiState = _uiState.asStateFlow()

    fun <T> executeInBackground(
        uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.LOADING),
        ...
        func: suspend () -> Resource<T>
    ) {
        viewModelScope.launch {
            val response = func()
            val newState = response.asUiState(checkEmptyList)
            if (showErrorDialog && newState == UiState.ERROR)
                showError(response.asErrorDialogDto())
            uiState.value = newState
        }
    }
}
```

**State açıqlama konvensiyası:**
- private `MutableStateFlow` + public `asStateFlow()`,
- one-shot event-lər (naviqasiya) üçün `MutableSharedFlow` + `asSharedFlow()`,
- data yükləmə `init {}` blokunda başlayır,
- nəticə `.onSuccess { }` / `.onError { message, _ -> }` ilə oxunur,
- kontekst üçün `SessionManager.companyId` / `warehouseId` istifadə olunur.

```15:49:app/src/main/java/eryaz/software/activegroup/ui/dashboard/inbound/crossdockList/CrossDockListVM.kt
class CrossDockListVM(private val repo: OrderRepo) : BaseViewModel() {

    private val _crossDockList = MutableStateFlow(listOf<CrossDockDto>())
    val crossDockList = _crossDockList.asStateFlow()

    init { fetchCrossDockList() }

    fun fetchCrossDockList() {
        executeInBackground(_uiState) {
            repo.fetchCrossDockList(
                companyId = SessionManager.companyId,
                warehouseId = SessionManager.warehouseId
            ).onSuccess {
                if (it.isEmpty()) { showWarning(WarningDialogDto(...)) }
                _crossDockList.emit(it)
            }.onError { message, _ -> showError(ErrorDialogDto(...)) }
        }
    }
}
```

---

## 8. XML UI Məntiqi

### DataBinding

Layout kökü `<layout>`, içində `<data><variable name="viewModel" .../>`.
Custom compound-view widget-lər `StateView` və `Toolbar` (bax `util/widgets/`)
və custom binding attribute-ları UI-ni idarə edir:

```6:28:app/src/main/res/layout/fragment_cross_dock_list.xml
    <data>
        <variable
            name="viewModel"
            type="...crossdockList.CrossDockListVM" />
    </data>

    <eryaz.software.activegroup.util.widgets.StateView
        ...
        app:onRetryClick="@{() -> viewModel.fetchCrossDockList()}"
        app:sv_viewState="@{viewModel.uiState}">

        <eryaz.software.activegroup.util.widgets.Toolbar
            app:navigationIcon="@drawable/back_icon"
            app:subTitle="@string/crossdock_list"
            app:title="@string/acceptance" />
```

`StateView` — `UiState`-ə görə loading / error / empty / content arasında keçid edir.

### Fragment pattern

`BaseFragment`-dən miras alınır, VM Koin `by viewModel<...>()` ilə gəlir,
ViewBinding lazy inflate olunur, `binding.viewModel` / `lifecycleOwner` bağlanır,
`subscribeToObservables()` / `setClicks()` override olunur. `StateFlow`-lar
`.asLiveData().observe(...)` ilə oxunur:

```14:51:app/src/main/java/eryaz/software/activegroup/ui/dashboard/inbound/crossdockList/CrossDockListFragment.kt
class CrossDockListFragment : BaseFragment() {

    override val viewModel by viewModel<CrossDockListVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE){
        FragmentCrossDockListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(...): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun subscribeToObservables() {
        viewModel.crossDockList.asLiveData().observe(viewLifecycleOwner){ adapter.submitList(it) }
    }

    private val adapter by lazy {
        CrossDockListAdapter().also { binding.recyclerView.adapter = it }
    }
}
```

### Adapter-lər

`ListAdapter<Dto, RecyclerView.ViewHolder>` + companion `DiffUtil.ItemCallback`,
`onItemClick: ((Dto) -> Unit) = {}` lambda property-si və `from(parent)` factory-li ViewHolder.
Yer: `util/adapter/**` və feature `adapter/` qovluqları.

```10:22:app/src/main/java/eryaz/software/activegroup/util/adapter/inbound/adapter/CrossDockListAdapter.kt
class CrossDockListAdapter :
    ListAdapter<CrossDockDto, RecyclerView.ViewHolder>(CrossDockDiffCallBack) {
    var onItemClick: ((CrossDockDto) -> Unit) = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CrossDockViewHolder.from(parent)
    override fun onBindViewHolder(holder: ..., position: Int) {
        when (holder) { is CrossDockViewHolder -> holder.bind(getItem(position), onItemClick) }
    }
}
```

**Naviqasiya:** Navigation Component — `res/navigation/` altında ~18 nav graph
(`navigation_main.xml` + feature başına `nav_*.xml`), SafeArgs ilə.

**Binding adapter-lər** (`util/bindingAdapter/`): `StateViewAdapter` (`UiState → ViewState`),
`BindingAdapters.kt` (~15 attribute: `itemDecoration`, `onSingleClick`, `isVisibleElseGone`,
`showSoftKeyboard`, `smoothProgress` və s.), `AnimationBindingAdapter`, `RadiusBindingAdapter`.

---

## 9. Dependency Injection — Koin

Framework: **Koin 3.1.4**. 3 modul, `ZeusApp`-də başladılır:

- **`appModuleApis`** (`data/di/AppModuleApis.kt`) — hər Retrofit service üçün
  `single { ZeusClient.provideXxxApi(androidContext()) }`.
- **`appModuleRepos`** (`data/di/AppModuleRepos.kt`) — hər repo üçün `factory { XxxRepo(get()) }`.

```12:26:data/src/main/java/eryaz/software/activegroup/data/di/AppModuleRepos.kt
val appModuleRepos = module {
    factory { AuthRepo(get()) }
    factory { UserRepo(get()) }
    factory { OrderRepo(get()) }
    factory { CountingRepo(get()) }
}
```

- **`appModule`** (`app/di/AppModule.kt`) — hər ViewModel `viewModel { }` ilə.
  Runtime arqumentlər üçün Koin parameter sintaksisi:

```82:86:app/src/main/java/eryaz/software/activegroup/di/AppModule.kt
    viewModel { (permissionType: DashboardPermissionType) ->
        DashboardDetailViewModel(
            repo = get(), permissionType = permissionType
        )
    }
```

Fragment-lər VM-i `by viewModel<T>()`, parametrli VM-lər isə
`by viewModel { parametersOf(...) }` ilə alır.

---

## 10. Şəbəkə (Networking) Quruluşu

Tək factory: `data/api/client/ZeusClient.kt`. `getRetrofit(context)` bir
`OkHttpClient` + `Retrofit` qurur; `provideXxxApi(context)` isə
`.create(Service::class.java)` çağırır.

```48:72:data/src/main/java/eryaz/software/activegroup/data/api/client/ZeusClient.kt
private fun getRetrofit(context: Context): Retrofit {
    val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(UnAuthorizedInterceptor())
        .addInterceptor(AuthorizationInterceptor())
        .addNetworkInterceptor(HttpLoggingInterceptor.getInterceptor())
        .build()
    val gson = GsonBuilder().setPrettyPrinting().create()
    return Retrofit.Builder()
        .baseUrl(getIpAddressTypeOutOrIn(context))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
}
```

- **Dinamik base URL** (`api/utils/NetworkUtils.kt`): cihazın Wi-Fi IP-sini oxuyur;
  lokal subnet-ə uyğundursa → `BASE_URL` (LAN), əks halda `BASE_OUT_URL` (public).
  Cleartext HTTP-ə icazə verilir (`usesCleartextTraffic="true"`).
- **Interceptor-lar:** `AuthorizationInterceptor` — `Content-Type: application/json`
  + `SessionManager.token`-dən `Authorization` header əlavə edir;
  `UnAuthorizedInterceptor` — 400/401 yoxlayır; `HttpLoggingInterceptor` — log.

```7:15:data/src/main/java/eryaz/software/activegroup/data/api/interceptors/AuthorizationInterceptor.kt
class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", SessionManager.token)
            .build()
        return chain.proceed(request)
    }
}
```

### Response wrapper-ları və `Resource`

`models/remote/`-də:
`open class BaseResponse(val success: Boolean)` → `data class ResultModel<T>(val result: T) : BaseResponse()`.
Xətalar: `ErrorResponse(error: ErrorModel)`.

`ResponseHandler` wrapper-ları `Resource` sealed tipinə çevirir:

```7:15:data/src/main/java/eryaz/software/activegroup/data/api/utils/Resource.kt
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()

    data class Error(
        val message: String? = "",
        val statusEnum: ResponseStatus = ResponseStatus.FAILED
    ) : Resource<Nothing>()
}
```

`onSuccess` / `onError` inline helper-ləri və `asUiState()` / `asErrorDialogDto()`
konverterləri `BaseViewModel` tərəfindən istifadə olunur.

---

## 11. Base Class-lar

| Class | Yer | Rol |
|-------|-----|-----|
| `BaseViewModel` | `app/ui/base/BaseViewModel.kt` | `executeInBackground`, ortaq error/warning/confirmation/progress `StateFlow`-lar, `_uiState`, `stringProvider` |
| `BaseFragment` | `app/ui/base/BaseFragment.kt` | VM dialog flow-larını avtomatik observe edir, `setClicks()`/`subscribeToObservables()` hook-ları, `playSound()` |
| `BaseActivity` | `app/ui/base/BaseActivity.kt` | `SessionManager.language`-dən locale, `getContentView()` abstract |
| `BaseDialogFragment` / `BaseBottomSheetDialogFragmentKt` | `app/ui/base/` | Dialog / bottom-sheet ekvivalentləri |
| `BaseRepo` | `data/repositories/BaseRepo.kt` | `callApi { }` — `Dispatchers.IO` + exception handling |
| `BaseResponse` | `data/models/remote/response/` | Bütün response-lar üçün `success` bayrağı |

`BaseFragment` VM→dialog əlaqəsini avtomatik qurur:

```64:86:app/src/main/java/eryaz/software/activegroup/ui/base/BaseFragment.kt
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel?.showErrorDialog?.observe(this, Lifecycle.State.RESUMED) { errorDialog.show(context, it) }
    viewModel?.showWarningDialog?.observe(this, Lifecycle.State.RESUMED) { warningDialog.show(context, it) }
    viewModel?.showConfirmationDialog?.observe(this, Lifecycle.State.RESUMED) { confirmationDialog.show(context, it) }
    viewModel?.showProgressDialog?.observe(this, Lifecycle.State.RESUMED) { progressDialog.setUiState(it) }
    viewModel?.stringProvider = { context?.getString(it).orEmpty() }
    setClicks()
    subscribeToObservables()
}
```

---

## 12. Köməkçi İnfrastruktur

- **`ZeusApp`** (`core/ZeusApp.kt`) — `MultiDexApplication`, 3 Koin modulu
  başladır, `SessionManager.init(...)`, Timber və qlobal uncaught-exception → Toast handler.
- **`SessionManager`** (`data/persistence/`) — `EncryptedSharedPreferences` üzərində
  `object` (fallback: adi prefs). token (set-də `"Bearer "` prefiksi əlavə olunur),
  userId, company/warehouse id+name, dil, app-lock bayrağı saxlayır.
- **`UiState` enum** — `SUCCESS, ERROR, LOADING, EMPTY` — reaktiv UI / `StateView`-ın əsası.

---

## 13. Uçdan-uca Data Axını (End-to-End)

```
Fragment (by viewModel)
   │
   ▼
ViewModel.executeInBackground { repo.method() }
   │
   ▼
Repo.callApi {
    api.call()                                  →  ResultModel<Response>
    ResponseHandler.handleSuccess(resp, resp.result.toDto())   ← MAPLEME burada
}
   │
   ▼
Resource<Dto>  (.onSuccess / .onError)
   │
   ▼
StateFlow / _uiState yenilənir
   │
   ▼
Fragment observe edir (asLiveData() / DataBinding)
   │
   ▼
StateView + ListAdapter render edir
```

> **Yekun qayda:** `*Response → *Dto` çevrilməsi (`toDto()` mapper-ləri) YALNIZ
> repository daxilində baş verir. Beləliklə UI qatı tamamilə DTO-yönümlü qalır
> və şəbəkə modelləri (`*Response`) UI-yə heç vaxt sızmır.
