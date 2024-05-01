package eryaz.software.activegroup.data.enums

enum class ResponseStatus(private val success: Boolean?) {
    OK(true),
    FAILED(false),
    NULL_DATA(null);
}