package eryaz.software.activegroup.data.models.dto

class ProcessDialog(
    val title: String? = "",
    var message: String? = "",
    var cancelable: Boolean = true,
    var showDialog: Boolean = true,
    var positiveButton: ButtonDto = ButtonDto(),
    var negativeButton: ButtonDto = ButtonDto(text = 0)
)