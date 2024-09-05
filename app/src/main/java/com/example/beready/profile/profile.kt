package com.example.beready.profile

data class profile(
    var name: String = "",
    var clgName: String? = null,
    var education: String? = null,
    var clgRoll: Int? = null,
    var resumeUrl: String? = null,
    var cgpa: Float? = null,
    var year: Int? = null,
    var skill: List<String>? = null,
    var internship: List<String>? = null,
    var projects: List<String>? = null,
    var projectlink: List<String>? = null,
    var badge: MutableList<String> = mutableListOf()
)


data class User(var name:String,var clgName: String?,var education: String?,var clgRoll: Int?,var resumeUrl: String?,var cgpa:Float?,
                var year:Int?, var skill:List<String>?,var internship:List<String>?,var projects:List<String>?,  var projectlink:List<String>?,var badge: MutableList<String>)
