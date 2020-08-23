package com.egoriku.mainscreen.presentation.viewmodel.dynamicFeature

import com.google.android.play.core.splitinstall.SplitInstallSessionState

sealed class ModuleStatus {
    object Available : ModuleStatus()
    data class Installing(val progress: Double) : ModuleStatus()
    object Unavailable : ModuleStatus()
    object Installed : ModuleStatus()
    class NeedsConfirmation(val state: SplitInstallSessionState) : ModuleStatus()
}