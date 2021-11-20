package com.github.psurkov.repeservice.security

import com.github.psurkov.repeservice.model.Student
import com.github.psurkov.repeservice.model.Tutor
import com.github.psurkov.repeservice.model.User
import com.github.psurkov.repeservice.repository.TutorRepository
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val tutorRepository: TutorRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val tutor = runBlocking {
            tutorRepository.findByUsername(username)
        }
        if (tutor == null) {
            throw UsernameNotFoundException("Cannot find user $username")
        }
        return UserDetailsImpl(tutor)
    }

    class UserDetailsImpl(private val user: User) : UserDetails {
        override fun getAuthorities() = listOf(
            SimpleGrantedAuthority(
                when (user) {
                    is Student -> "STUDENT"
                    is Tutor -> "TUTOR"
                }
            )
        )

        override fun getPassword() = user.password

        override fun getUsername() = user.username

        override fun isAccountNonExpired() = true

        override fun isAccountNonLocked() = true

        override fun isCredentialsNonExpired() = true

        override fun isEnabled() = true
    }
}