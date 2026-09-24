package ru.itmo.notifications.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UserProfileCacheSettings(
    @Value("\${app.cache.user-profile.enabled}") @Volatile var enabled: Boolean,
)
