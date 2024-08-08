package com.example.OAuth.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_user")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val email: String,
    val provider: String
)
