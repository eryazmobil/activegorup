package eryaz.software.activegroup.ui

import eryaz.software.activegroup.util.SingleLiveEvent

object EventBus {
    val navigateToSplash = SingleLiveEvent<Boolean>()
}
