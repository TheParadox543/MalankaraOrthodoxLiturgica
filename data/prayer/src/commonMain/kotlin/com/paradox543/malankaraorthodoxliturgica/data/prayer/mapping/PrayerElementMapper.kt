package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

// Helper extensions to reduce duplication for DynamicSong conversions
private fun PrayerElementDto.DynamicSong.toDomainSong(): PrayerElement.DynamicSong =
    PrayerElement.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toDomain() },
    )

private fun PrayerElement.DynamicSong.toDataSong(): PrayerElementDto.DynamicSong =
    PrayerElementDto.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toData() },
    )

// Extension-based mappers: data -> domain
fun PrayerElementDto.toDomain(): PrayerElement =
    when (this) {
        is PrayerElementDto.Title -> {
            PrayerElement.Title(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Heading -> {
            PrayerElement.Heading(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Subheading -> {
            PrayerElement.Subheading(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Prose -> {
            PrayerElement.Prose(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Song -> {
            PrayerElement.Song(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Subtext -> {
            PrayerElement.Subtext(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Source -> {
            PrayerElement.Source(content.applyPrayerReplacements())
        }

        is PrayerElementDto.Button -> {
            PrayerElement.Button(
                link = link,
                label = label?.applyPrayerReplacements(),
                replace = replace,
            )
        }

        is PrayerElementDto.Link -> {
            PrayerElement.Link(file)
        }

        is PrayerElementDto.LinkCollapsible -> {
            PrayerElement.LinkCollapsible(file)
        }

        is PrayerElementDto.CollapsibleBlock -> {
            PrayerElement.CollapsibleBlock(
                title = title,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementDto.DynamicSong -> {
            this.toDomainSong()
        }

        is PrayerElementDto.DynamicSongsBlock -> {
            PrayerElement.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDomainSong() }.toMutableList(),
                defaultContent = defaultContent?.toDomainSong(),
            )
        }

        is PrayerElementDto.AlternativeOption -> {
            PrayerElement.AlternativeOption(
                label = label,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementDto.AlternativePrayersBlock -> {
            PrayerElement.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt ->
                        PrayerElement.AlternativeOption(opt.label, opt.items.map { it.toDomain() })
                    },
            )
        }

        is PrayerElementDto.Error -> {
            PrayerElement.Error(content)
        }
    }

fun List<PrayerElementDto>.toDomainList(): List<PrayerElement> = map { it.toDomain() }

// Reverse mapping: domain -> data
fun PrayerElement.toData(): PrayerElementDto =
    when (this) {
        is PrayerElement.Title -> {
            PrayerElementDto.Title(content)
        }

        is PrayerElement.Heading -> {
            PrayerElementDto.Heading(content)
        }

        is PrayerElement.Subheading -> {
            PrayerElementDto.Subheading(content)
        }

        is PrayerElement.Prose -> {
            PrayerElementDto.Prose(content)
        }

        is PrayerElement.Song -> {
            PrayerElementDto.Song(content)
        }

        is PrayerElement.Subtext -> {
            PrayerElementDto.Subtext(content)
        }

        is PrayerElement.Source -> {
            PrayerElementDto.Source(content)
        }

        is PrayerElement.Button -> {
            PrayerElementDto.Button(
                link = link,
                label = label,
                replace = replace,
            )
        }

        is PrayerElement.Link -> {
            PrayerElementDto.Link(file)
        }

        is PrayerElement.LinkCollapsible -> {
            PrayerElementDto.LinkCollapsible(file)
        }

        is PrayerElement.CollapsibleBlock -> {
            PrayerElementDto.CollapsibleBlock(
                title = title,
                items = items.map { it.toData() },
            )
        }

        is PrayerElement.DynamicSong -> {
            this.toDataSong()
        }

        is PrayerElement.DynamicSongsBlock -> {
            PrayerElementDto.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDataSong() }.toMutableList(),
                defaultContent = defaultContent?.toDataSong(),
            )
        }

        is PrayerElement.AlternativeOption -> {
            PrayerElementDto.AlternativeOption(
                label = label,
                items = items.map { it.toData() },
            )
        }

        is PrayerElement.AlternativePrayersBlock -> {
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

        is PrayerElement.Error -> {
            PrayerElementDto.Error(content)
        }
    }

fun List<PrayerElement>.toDataList(): List<PrayerElementDto> = map { it.toData() }