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

        val oAuthProfile = extractProfile(provider, attributes)

        val user =
            userRepository.findByProviderAndProviderId(provider, oAuthProfile.providerId)
                ?: userRepository.save(
                    User(
                        email = oAuthProfile.email,
                        name = oAuthProfile.name,
                        provider = provider,
                        providerId = oAuthProfile.providerId,
                        image = oAuthProfile.image,
                    ),
                )

        val mutableAttributes = attributes.toMutableMap()
        mutableAttributes["userId"] = user.id.toString()

        return DefaultOAuth2User(
            oAuth2User.authorities,
            mutableAttributes,
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName,
        )
    }

    private fun extractProfile(
        provider: String,
        attributes: Map<String, Any>,
    ): OAuthProfile =
        when (provider) {
            "google" ->
                OAuthProfile(
                    providerId = attributes["sub"] as String,
                    email = attributes["email"] as String,
                    name = attributes["name"] as? String ?: attributes["email"] as String,
                    image = attributes["picture"] as? String,
                )
            "kakao" -> {
                val kakaoAccount = attributes["kakao_account"] as? Map<*, *> ?: emptyMap<String, Any>()
                val profile = kakaoAccount["profile"] as? Map<*, *> ?: emptyMap<String, Any>()
                OAuthProfile(
                    providerId = attributes["id"].toString(),
                    email = kakaoAccount["email"] as? String ?: "",
                    name = profile["nickname"] as? String ?: "",
                    image = profile["profile_image_url"] as? String,
                )
            }
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }

    private data class OAuthProfile(
        val providerId: String,
        val email: String,
        val name: String,
        val image: String?,
    )
}
