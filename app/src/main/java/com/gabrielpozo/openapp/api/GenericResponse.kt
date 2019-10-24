package com.gabrielpozo.openapp.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GenericResponse(
    @SerializedName(value = "response")
    @Expose
    var response: String
) {}