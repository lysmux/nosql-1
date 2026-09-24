package ru.itmo.notifications.entrypoint.rest.resource.settings

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.SettingsApi
import ru.itmo.notifications.entrypoint.api.model.CacheSettingsDto
import ru.itmo.notifications.service.UserProfileCacheSettings

@RestController
class SettingsController(
    private val cacheSettings: UserProfileCacheSettings,
) : SettingsApi {
    override fun settingsReadCache(): ResponseEntity<CacheSettingsDto> =
        ResponseEntity.ok(CacheSettingsDto(cacheSettings.enabled))

    override fun settingsUpdateCache(cacheSettingsDto: CacheSettingsDto): ResponseEntity<CacheSettingsDto> {
        cacheSettings.enabled = cacheSettingsDto.enabled
        return ResponseEntity.ok(cacheSettingsDto)
    }
}
