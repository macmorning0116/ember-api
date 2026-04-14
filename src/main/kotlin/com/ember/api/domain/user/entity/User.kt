package com.ember.api.domain.user.entity

import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    val provider: String,
    @Column(nullable = false)
    val providerId: String,
) : BaseEntity()
