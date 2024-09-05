package com.example.beready.community

class Message{
    var message: String? = null
    var name: String? = null
    var key: String?=null

    constructor(){}

    constructor(message: String?, name: String?,key:String?){
        this.message = message
        this.name = name
        this.key=key
    }
    override fun toString(): String {
        return "Message(" +
                "message='$message'" +
                "name='$name', " +
                "key='$key'" +

                ")"
    }
}