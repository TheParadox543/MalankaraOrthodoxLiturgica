package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain

// Helper extensions to reduce duplication for DynamicSong conversions
private fun PrayerElementData.DynamicSong.toDomainSong(): PrayerElementDomain.DynamicSong =
    PrayerElementDomain.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toDomain() },
    )

private fun PrayerElementDomain.DynamicSong.toDataSong(): PrayerElementData.DynamicSong =
    PrayerElementData.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toData() },
    )

// Extension-based mappers: data -> domain
fun PrayerElementData.toDomain(): PrayerElementDomain =
    when (this) {
        is PrayerElementData.Title -> {
            PrayerElementDomain.Title(content)
        }

        is PrayerElementData.Heading -> {
            PrayerElementDomain.Heading(content)
        }

        is PrayerElementData.Subheading -> {
            PrayerElementDomain.Subheading(content)
        }

        is PrayerElementData.Prose -> {
            PrayerElementDomain.Prose(content)
        }

        is PrayerElementData.Song -> {
            PrayerElementDomain.Song(content)
        }

        is PrayerElementData.Subtext -> {
            PrayerElementDomain.Subtext(content)
        }

        is PrayerElementData.Source -> {
            PrayerElementDomain.Source(content)
        }

        is PrayerElementData.Button -> {
            PrayerElementDomain.Button(
                link = link,
                label = label,
                replace = replace,
            )
        }

        is PrayerElementData.Link -> {
            PrayerElementDomain.Link(file)
        }

        is PrayerElementData.LinkCollapsible -> {
            PrayerElementDomain.LinkCollapsible(file)
        }

        is PrayerElementData.CollapsibleBlock -> {
            PrayerElementDomain.CollapsibleBlock(
                title = title,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementData.DynamicSong -> {
            this.toDomainSong()
        }

        is PrayerElementData.DynamicSongsBlock -> {
            PrayerElementDomain.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDomainSong() }.toMutableList(),
                defaultContent = defaultContent?.toDomainSong(),
            )
        }

        is PrayerElementData.AlternativeOption -> {
            PrayerElementDomain.AlternativeOption(
                label = label,
                items = items.map { it.toDomain() },
            )
        }

        is PrayerElementData.AlternativePrayersBlock -> {
            PrayerElementDomain.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt ->
                        PrayerElementDomain.AlternativeOption(opt.label, opt.items.map { it.toDomain() })
                    },
            )
        }

        is PrayerElementData.Error -> {
            PrayerElementDomain.Error(content)
        }
    }

fun List<PrayerElementData>.toDomainList(): List<PrayerElementDomain> = map { it.toDomain() }

// Reverse mapping: domain -> data
fun PrayerElementDomain.toData(): PrayerElementData =
    when (this) {
        is PrayerElementDomain.Title -> {
            PrayerElementData.Title(content)
        }

        is PrayerElementDomain.Heading -> {
            PrayerElementData.Heading(content)
        }

        is PrayerElementDomain.Subheading -> {
            PrayerElementData.Subheading(content)
        }

        is PrayerElementDomain.Prose -> {
            PrayerElementData.Prose(content)
        }

        is PrayerElementDomain.Song -> {
            PrayerElementData.Song(content)
        }

        is PrayerElementDomain.Subtext -> {
            PrayerElementData.Subtext(content)
        }

        is PrayerElementDomain.Source -> {
            PrayerElementData.Source(content)
        }

        is PrayerElementDomain.Button -> {
            PrayerElementData.Button(
                link = link,
                label = label,
                replace = replace,
            )
        }

        is PrayerElementDomain.Link -> {
            PrayerElementData.Link(file)
        }

        is PrayerElementDomain.LinkCollapsible -> {
            PrayerElementData.LinkCollapsible(file)
        }

        is PrayerElementDomain.CollapsibleBlock -> {
            PrayerElementData.CollapsibleBlock(
                title = title,
                items = items.map { it.toData() },
            )
        }

        is PrayerElementDomain.DynamicSong -> {
            this.toDataSong()
        }

        is PrayerElementDomain.DynamicSongsBlock -> {
            PrayerElementData.DynamicSongsBlock(
                timeKey = timeKey,
                items = items.map { ds -> ds.toDataSong() }.toMutableList(),
                defaultContent = defaultContent?.toDataSong(),
            )
        }

        is PrayerElementDomain.AlternativeOption -> {
            PrayerElementData.AlternativeOption(
                label = label,
                items = items.map { it.toData() },
            )
        }

        is PrayerElementDomain.AlternativePrayersBlock -> {
            PrayerElementData.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt ->
                        PrayerElementData.AlternativeOption(
                            opt.label,
                            opt.items.map { it.toData() },
                        )
                    },
            )
        }

        is PrayerElementDomain.Error -> {
            PrayerElementData.Error(content)
        }
    }

fun List<PrayerElementDomain>.toDataList(): List<PrayerElementData> = map { it.toData() }