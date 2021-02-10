package com.mathewsachin.fategrandautomata.scripts.models

import com.mathewsachin.fategrandautomata.scripts.enums.GameServerEnum
import com.mathewsachin.fategrandautomata.scripts.modules.Phone
import com.mathewsachin.libautomata.Location
import com.mathewsachin.libautomata.Region

sealed class Skill(
    val clickLocation: Location,
    val autoSkillCode: Char
) {
    class Servant private constructor(
        clickLocation: Location,
        autoSkillCode: Char
    ) : Skill(clickLocation, autoSkillCode) {
        companion object {
            val jpfs get() = when {
                Phone.s.contains("Pixel 4 XL") -> 0
                Phone.s.contains("SM-G975U") -> 100
                else -> 0
            }
            val list = listOf(
                Servant(Location(jpfs+0, 1100), 'a'),
                Servant(Location(jpfs+180, 1100), 'b'),
                Servant(Location(jpfs+360, 1100), 'c'),

                Servant(Location(jpfs+640, 1100), 'd'),
                Servant(Location(jpfs+820, 1100), 'e'),
                Servant(Location(jpfs+990, 1100), 'f'),

                Servant(Location(jpfs+1270, 1100), 'g'),
                Servant(Location(jpfs+1450, 1100), 'h'),
                Servant(Location(jpfs+1630, 1100), 'i')
            )
        }
    }

    class Master private constructor(
        clickLocation: Location,
        autoSkillCode: Char
    ) : Skill(clickLocation, autoSkillCode) {
        companion object {
            val jpfs get() = when {
                Phone.s.contains("Pixel 4 XL") -> 0
                Phone.s.contains("SM-G975U") -> -100
                else -> -70
            }
            val list = listOf(
                Master(Location(jpfs+1950, 640), 'j'),
                Master(Location(jpfs+2120, 640), 'k'),
                Master(Location(jpfs+2300, 640), 'l')
            )
        }
    }
}