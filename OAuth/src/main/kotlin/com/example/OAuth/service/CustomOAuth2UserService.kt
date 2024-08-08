package com.example.OAuth.service

import com.example.OAuth.model.User
import com.example.OAuth.repository.UserRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CustomOAuth2UserService(private val userRepository: UserRepository) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val attributes = oAuth2User.attributes.toMutableMap()

        val provider = userRequest.clientRegistration.registrationId
        val email: String?
        val name: String?

        if (provider == "google") {
            email = attributes["email"] as? String
            name = attributes["name"] as? String
        } else if (provider == "github") {
            name = attributes["login"] as? String
            email = attributes["email"] as? String ?: fetchGithubEmail(userRequest.accessToken.tokenValue)
        } else {
            throw IllegalArgumentException("Unsupported provider: $provider")
        }

        if (email == null || name == null) {
            throw IllegalArgumentException("Failed to retrieve necessary user information from $provider")
        }

        attributes["name"] = name
        attributes["email"] = email
        attributes["provider"] = provider

        var user = userRepository.findByEmail(email)
        if (user == null) {
            user = User(name = name, email = email, provider = provider)
            userRepository.save(user)
        }

        return DefaultOAuth2User(oAuth2User.authorities, attributes, "name")
    }

    private fun fetchGithubEmail(accessToken: String): String? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://api.github.com/user/emails",
            HttpMethod.GET,
            entity,
            Array<GithubEmail>::class.java
        )
        val emails = response.body
        return emails?.firstOrNull { it.primary && it.verified }?.email
    }

    data class GithubEmail(
        val email: String,
        val primary: Boolean,
        val verified: Boolean
    )
}