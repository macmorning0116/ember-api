package com.ember.api.domain.application.entity

import com.ember.api.domain.user.entity.User
import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "applications")
@SQLRestriction("deleted_at IS NULL")
class Application(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Column(nullable = false)
    var companyName: String,
    @Column(nullable = false)
    var careerLevel: String,
    var deadline: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var companySize: CompanySize,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ApplicationStatus,
    var url: String? = null,
    var deletedAt: LocalDateTime? = null,
    @OneToMany(
        mappedBy = "application",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val coverLetters: MutableList<CoverLetter> = mutableListOf(),
) : BaseEntity() {
    fun softDelete() {
        deletedAt = LocalDateTime.now()
    }
}
