package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain

// Extension-based mappers: data -> domain
fun PrayerElementData.toDomain(): PrayerElementDomain =
    when (this) {
        is PrayerElementData.Title -> PrayerElementDomain.Title(content)
        is PrayerElementData.Heading -> PrayerElementDomain.Heading(content)
        is PrayerElementData.Subheading -> PrayerElementDomain.Subheading(content)
        is PrayerElementData.Prose -> PrayerElementDomain.Prose(content)
        is PrayerElementData.Song -> PrayerElementDomain.Song(content)
        is PrayerElementData.Subtext -> PrayerElementDomain.Subtext(content)
        is PrayerElementData.Source -> PrayerElementDomain.Source(content)
        is PrayerElementData.Button ->
            PrayerElementDomain.Button(
                link = link,
                label = label,
                replace = replace,
            )
        is PrayerElementData.Link -> PrayerElementDomain.Link(file)
        is PrayerElementData.LinkCollapsible -> PrayerElementDomain.LinkCollapsible(file)
        is PrayerElementData.CollapsibleBlock ->
            PrayerElementDomain.CollapsibleBlock(
                title = title,
                items = items.map { it.toDomain() },
            )
        is PrayerElementData.DynamicSong ->
            PrayerElementDomain.DynamicSong(
                eventKey = eventKey,
                eventTitle = eventTitle,
                timeKey = timeKey,
                items = items.map { it.toDomain() },
            )
        is PrayerElementData.DynamicSongsBlock ->
            PrayerElementDomain.DynamicSongsBlock(
                timeKey = timeKey,
                items =
                    items
                        .map { ds: PrayerElementData.DynamicSong ->
                            PrayerElementDomain.DynamicSong(
                                eventKey = ds.eventKey,
                                eventTitle = ds.eventTitle,
                                timeKey = ds.timeKey,
                                items = ds.items.map { it.toDomain() },
                            )
                        }.toMutableList(),
                defaultContent =
                    defaultContent?.let { ds: PrayerElementData.DynamicSong ->
                        PrayerElementDomain.DynamicSong(
                            eventKey = ds.eventKey,
                            eventTitle = ds.eventTitle,
                            timeKey = ds.timeKey,
                            items = ds.items.map { it.toDomain() },
                        )
                    },
            )
        is PrayerElementData.AlternativeOption ->
            PrayerElementDomain.AlternativeOption(
                label = label,
                items = items.map { it.toDomain() },
            )
        is PrayerElementData.AlternativePrayersBlock ->
            PrayerElementDomain.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt: PrayerElementData.AlternativeOption ->
                        PrayerElementDomain.AlternativeOption(opt.label, opt.items.map { it.toDomain() })
                    },
            )
        is PrayerElementData.Error -> PrayerElementDomain.Error(content)
    }

fun List<PrayerElementData>.toDomainList(): List<PrayerElementDomain> = map { it.toDomain() }

// Reverse mapping: domain -> data
fun PrayerElementDomain.toData(): PrayerElementData =
    when (this) {
        is PrayerElementDomain.Title -> PrayerElementData.Title(content)
        is PrayerElementDomain.Heading -> PrayerElementData.Heading(content)
        is PrayerElementDomain.Subheading -> PrayerElementData.Subheading(content)
        is PrayerElementDomain.Prose -> PrayerElementData.Prose(content)
        is PrayerElementDomain.Song -> PrayerElementData.Song(content)
        is PrayerElementDomain.Subtext -> PrayerElementData.Subtext(content)
        is PrayerElementDomain.Source -> PrayerElementData.Source(content)
        is PrayerElementDomain.Button ->
            PrayerElementData.Button(
                link = link,
                label = label,
                replace = replace,
            )
        is PrayerElementDomain.Link -> PrayerElementData.Link(file)
        is PrayerElementDomain.LinkCollapsible -> PrayerElementData.LinkCollapsible(file)
        is PrayerElementDomain.CollapsibleBlock ->
            PrayerElementData.CollapsibleBlock(
                title = title,
                items = items.map { it.toData() },
            )
        is PrayerElementDomain.DynamicSong ->
            PrayerElementData.DynamicSong(
                eventKey = eventKey,
                eventTitle = eventTitle,
                timeKey = timeKey,
                items = items.map { it.toData() },
            )
        is PrayerElementDomain.DynamicSongsBlock ->
            PrayerElementData.DynamicSongsBlock(
                timeKey = timeKey,
                items =
                    items
                        .map { ds: PrayerElementDomain.DynamicSong ->
                            PrayerElementData.DynamicSong(
                                eventKey = ds.eventKey,
                                eventTitle = ds.eventTitle,
                                timeKey = ds.timeKey,
                                items = ds.items.map { it.toData() },
                            )
                        }.toMutableList(),
                defaultContent =
                    defaultContent?.let { ds: PrayerElementDomain.DynamicSong ->
                        PrayerElementData.DynamicSong(
                            eventKey = ds.eventKey,
                            eventTitle = ds.eventTitle,
                            timeKey = ds.timeKey,
                            items = ds.items.map { it.toData() },
                        )
                    },
            )
        is PrayerElementDomain.AlternativeOption ->
            PrayerElementData.AlternativeOption(
                label = label,
                items = items.map { it.toData() },
            )
        is PrayerElementDomain.AlternativePrayersBlock ->
            PrayerElementData.AlternativePrayersBlock(
                title = title,
                options =
                    options.map { opt: PrayerElementDomain.AlternativeOption ->
                        PrayerElementData.AlternativeOption(opt.label, opt.items.map { it.toData() })
                    },
            )
        is PrayerElementDomain.Error -> PrayerElementData.Error(content)
    }

fun List<PrayerElementDomain>.toDataList(): List<PrayerElementData> = map { it.toData() }
