package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

// Helper extensions to reduce duplication for DynamicSong conversions
private fun PrayerElementDto.DynamicSong.toDomainSong(): PrayerElement.DynamicSong =
    PrayerElement.DynamicSong(
        eventKey = eventKey,
        eventTitle = eventTitle,
        timeKey = timeKey,
        items = items.map { it.toDomain() },
    )

// Map the nested Bible reference DTOs found inside prayer JSON -> domain bible models
private fun PrayerElementDto.ReferenceRange.toDomainRef(): ReferenceRange =
    ReferenceRange(
        startChapter = startChapter,
        endChapter = endChapter,
        startVerse = startVerse,
        endVerse = endVerse,
    )

private fun PrayerElementDto.BibleReference.toDomainBibleReference(): BibleReference =
    BibleReference(
        bookNumber = bookNumber,
        ranges = ranges.map { it.toDomainRef() },
    )

private fun List<PrayerElementDto.BibleReference>.toDomainBibleReferences(): List<BibleReference> = map { it.toDomainBibleReference() }

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

        is PrayerElementDto.PrayerBibleReading -> {
            PrayerElement.PrayerBibleReading(readings.toDomainBibleReferences())
        }

        is PrayerElementDto.BibleReference -> {
            // This DTO type is used only as a nested type inside PrayerBibleReading,
            // not as a standalone PrayerElement. Handle it defensively here.
            PrayerElement.Error("Unexpected bible reference element in prayer content")
        }

        is PrayerElementDto.Error -> {
            PrayerElement.Error(content)
        }
    }

fun List<PrayerElementDto>.toDomainList(): List<PrayerElement> = map { it.toDomain() }
