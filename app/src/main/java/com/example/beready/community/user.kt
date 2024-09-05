package com.example.beready.community

class User {
    var name:String?=null
    var email:String? = null
    var uid:String? = null

    constructor() {}

    constructor(name:String?,email:String?, password:String?){
        this.name=name
        this.email= email
        this.uid = password
    }
    override fun toString(): String {
        return "User(" +
                "name='$uid', " +
                "uid='$uid', " +
                "email='$email'" +
                ")"
    }
}