package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain

// Helper extensions to reduce duplication for DynamicSong conversions
private fun PrayerElementDto.DynamicSong.toDomainSong(): PrayerElementDomain.DynamicSong =
    PrayerElementDomain.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toDomain() },
    )

private fun PrayerElementDomain.DynamicSong.toDataSong(): PrayerElementDto.DynamicSong =
    PrayerElementDto.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toData() },
    )

// Extension-based mappers: data -> domain
fun PrayerElementDto.toDomain(): PrayerElementDomain =
    when (this) {
        is PrayerElementDto.Title -> {
            PrayerElementDomain.Title(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Heading -> {
            PrayerElementDomain.Heading(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Subheading -> {
            PrayerElementDomain.Subheading(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Prose -> {
            PrayerElementDomain.Prose(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Song -> {
            PrayerElementDomain.Song(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Subtext -> {
            PrayerElementDomain.Subtext(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Source -> {
            PrayerElementDomain.Source(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Button -> {
            PrayerElementDomain.Button(
                link = link,
                label = label?.applyPrayerReplacements(),
                replace = replace,
            )
        }

        is PrayerElementDto.Link -> {
            PrayerElementDomain.Link(file)
        }

        is PrayerElementDto.LinkCollapsible -> {
            PrayerElementDomain.LinkCollapsible(file)
        }

        is PrayerElementDto.CollapsibleBlock -> {
            PrayerElementDomain.CollapsibleBlock(
                title = title,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementDto.DynamicSong -> {
            this.toDomainSong()
        }

        is PrayerElementDto.DynamicSongsBlock -> {
            PrayerElementDomain.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDomainSong() }.toMutableList(),
                defaultContent = defaultContent?.toDomainSong(),
            )
        }

        is PrayerElementDto.AlternativeOption -> {
            PrayerElementDomain.AlternativeOption(
                label = label,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementDto.AlternativePrayersBlock -> {
            PrayerElementDomain.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt ->
                        PrayerElementDomain.AlternativeOption(opt.label, opt.items.map { it.toDomain() })
                    },
            )
        }

        is PrayerElementDto.Error -> {
            PrayerElementDomain.Error(content)
        }
    }

fun List<PrayerElementDto>.toDomainList(): List<PrayerElementDomain> = map { it.toDomain() }

// Reverse mapping: domain -> data
fun PrayerElementDomain.toData(): PrayerElementDto =
    when (this) {
        is PrayerElementDomain.Title -> {
            PrayerElementDto.Title(content)
        }

        is PrayerElementDomain.Heading -> {
            PrayerElementDto.Heading(content)
        }

        is PrayerElementDomain.Subheading -> {
            PrayerElementDto.Subheading(content)
        }

        is PrayerElementDomain.Prose -> {
            PrayerElementDto.Prose(content)
        }

        is PrayerElementDomain.Song -> {
            PrayerElementDto.Song(content)
        }

        is PrayerElementDomain.Subtext -> {
            PrayerElementDto.Subtext(content)
        }

        is PrayerElementDomain.Source -> {
            PrayerElementDto.Source(content)
        }

        is PrayerElementDomain.Button -> {
            PrayerElementDto.Button(
                link = link,
                label = label,
                replace = replace,
            )
        }

        is PrayerElementDomain.Link -> {
            PrayerElementDto.Link(file)
        }

        is PrayerElementDomain.LinkCollapsible -> {
            PrayerElementDto.LinkCollapsible(file)
        }

        is PrayerElementDomain.CollapsibleBlock -> {
            PrayerElementDto.CollapsibleBlock(
                title = title,
                items = items.map { it.toData() },
            )
        }

        is PrayerElementDomain.DynamicSong -> {
            this.toDataSong()
        }

        is PrayerElementDomain.DynamicSongsBlock -> {
            PrayerElementDto.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDataSong() }.toMutableList(),
                defaultContent = defaultContent?.toDataSong(),
            )
        }

        is PrayerElementDomain.AlternativeOption -> {
            PrayerElementDto.AlternativeOption(
                label = label,
                items = items.map { it.toData() },
            )
        }

        is PrayerElementDomain.AlternativePrayersBlock -> {
            PrayerElementDto.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt ->
                        PrayerElementDto.AlternativeOption(
                            opt.label,
                            opt.items.map { it.toData() },
                        )
                    },
            )
        }

        is PrayerElementDomain.Error -> {
            PrayerElementDto.Error(content)
        }
    }

fun List<PrayerElementDomain>.toDataList(): List<PrayerElementDto> = map { it.toData() }