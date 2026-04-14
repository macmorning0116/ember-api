package com.ember.api.domain.auth.oauth

import com.ember.api.domain.user.entity.User
import com.ember.api.domain.user.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = userRequest.clientRegistration.registrationId

        val attributes = oAuth2User.attributes
        val providerId = attributes["sub"] as String
        val email = attributes["email"] as String
        val name = attributes["name"] as? String ?: email
        val image = attributes["picture"] as? String

        val user =
            userRepository.findByProviderAndProviderId(provider, providerId)
                ?: userRepository.save(
                    User(
                        email = email,
                        name = name,
                        provider = provider,
                        providerId = providerId,
                        image = image,
                    ),
                )

        val mutableAttributes = attributes.toMutableMap()
        mutableAttributes["userId"] = user.id.toString()

        return DefaultOAuth2User(
            oAuth2User.authorities,
            mutableAttributes,
            "email",
        )
    }
}
