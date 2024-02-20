package com.kindeev.swipelauncher.data.settings

import java.io.Serializable

data class SettingValue(val type: SettingTypes, val data: Any? = null): Serializable
