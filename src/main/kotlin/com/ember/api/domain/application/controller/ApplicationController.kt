package com.ember.api.domain.application.controller

import com.ember.api.domain.application.dto.ApplicationResponse
import com.ember.api.domain.application.dto.CreateApplicationRequest
import com.ember.api.domain.application.dto.UpdateApplicationRequest
import com.ember.api.domain.application.service.ApplicationService
import com.ember.api.global.response.ApiResponse
import com.ember.api.global.security.CurrentUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/applications")
class ApplicationController(
    private val applicationService: ApplicationService,
) {
    @GetMapping
    fun getApplications(): ResponseEntity<ApiResponse<List<ApplicationResponse>>> {
        val userId = CurrentUser.getUserId()
        val applications = applicationService.getApplications(userId)
        return ResponseEntity.ok(ApiResponse.success(applications))
    }

    @PostMapping
    fun createApplication(
        @Valid @RequestBody request: CreateApplicationRequest,
    ): ResponseEntity<ApiResponse<ApplicationResponse>> {
        val userId = CurrentUser.getUserId()
        val application = applicationService.createApplication(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(application))
    }

    @GetMapping("/{id}")
    fun getApplication(
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<ApplicationResponse>> {
        val userId = CurrentUser.getUserId()
        val application = applicationService.getApplication(id, userId)
        return ResponseEntity.ok(ApiResponse.success(application))
    }

    @PatchMapping("/{id}")
    fun updateApplication(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateApplicationRequest,
    ): ResponseEntity<ApiResponse<ApplicationResponse>> {
        val userId = CurrentUser.getUserId()
        val application = applicationService.updateApplication(id, userId, request)
        return ResponseEntity.ok(ApiResponse.success(application))
    }

    @DeleteMapping("/{id}")
    fun deleteApplication(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        val userId = CurrentUser.getUserId()
        applicationService.deleteApplication(id, userId)
        return ResponseEntity.noContent().build()
    }
}
