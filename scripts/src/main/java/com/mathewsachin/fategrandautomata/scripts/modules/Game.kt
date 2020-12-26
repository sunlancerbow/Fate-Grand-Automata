package com.mathewsachin.fategrandautomata.scripts.modules

import com.mathewsachin.fategrandautomata.scripts.IFgoAutomataApi
import com.mathewsachin.fategrandautomata.scripts.enums.GameServerEnum
import com.mathewsachin.fategrandautomata.scripts.enums.RefillResourceEnum
import com.mathewsachin.fategrandautomata.scripts.isWide
import com.mathewsachin.fategrandautomata.scripts.models.*
import com.mathewsachin.fategrandautomata.scripts.prefs.IPreferences
import com.mathewsachin.libautomata.GameAreaManager
import com.mathewsachin.libautomata.Location
import com.mathewsachin.libautomata.Region
import com.mathewsachin.libautomata.dagger.ScriptScope
import com.mathewsachin.libautomata.extensions.ITransformationExtensions
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.seconds

fun IFgoAutomataApi.needsToRetry() = images.retry in game.retryRegion

fun IFgoAutomataApi.retry() {
    game.retryRegion.click()

    2.seconds.wait()
}

@ScriptScope
class Game @Inject constructor(
    val prefs: IPreferences,
    transformationExtensions: ITransformationExtensions,
    val gameAreaManager: GameAreaManager
) {
    companion object {
        val menuScreenRegion = Region(2100, 1200, 1000, 1000)
        val menuStorySkipRegion = Region(2240, 20, 300, 120)
        val menuSelectQuestClick = Location(2290, 440)
        val menuStorySkipClick = Location(2360, 80)

        val supportNotFoundRegion = Region(468, 708, 100, 90)

        val battleBack = Location(2400, 1370)

        val resultScreenRegion = Region(100, 300, 700, 200)
        val resultBondRegion = Region(2000, 750, 120, 190)
        val resultMasterExpRegion = Region(1280, 350, 400, 110)
        val resultMatRewardsRegion = Region(2080, 1220, 280, 200)
        val resultMasterLvlUpRegion = Region(1990, 160, 250, 270)

        val resultCeRewardRegion = Region(1050, 1216, 33, 28)
        val resultCeRewardDetailsRegion = Region(0, 512, 135, 115)
        val resultCeRewardCloseClick = Location(80, 60)

        val resultQuestRewardRegion = Region(1630, 140, 370, 250)
        val resultClick = Location(1600, 1350)
        val resultNextClick = Location(2200, 1350) // see docs/quest_result_next_click.png
        val resultDropScrollbarRegion = Region(2260, 230, 100, 88)

        val gudaFinalRewardsRegion = Region(1160, 1040, 228, 76)

        val finishedLotteryBoxRegion = Region(500, 860, 180, 100)
    }

    val scriptArea =
        Region(
            Location(),
            gameAreaManager.gameArea.size * (1 / transformationExtensions.scriptToScreenScale())
        )

    val isWide = prefs.gameServer == GameServerEnum.Jp
            && scriptArea.isWide()

    fun Location.xFromCenter() =
        this + Location(scriptArea.center.X, 0)

    fun Region.xFromCenter() =
        this + Location(scriptArea.center.X, 0)

    fun Location.xFromRight() =
        this + Location(scriptArea.right, 0)

    fun Region.xFromRight() =
        this + Location(scriptArea.right, 0)

    fun Location.yFromBottom() =
        this + Location(0, scriptArea.bottom)

    fun Region.yFromBottom() =
        this + Location(0, scriptArea.bottom)

    val continueRegion = Region(120, 1000, 800, 200).xFromCenter()
    val continueBoostClick = Location(-20, 1120).xFromCenter()
    val continueClick = Location(370, 1120).xFromCenter()

    val inventoryFullRegion = Region(-230, 900, 458, 90).xFromCenter()

    val menuStartQuestClick =
        (if (isWide)
            Location(-350, -160)
        else Location(-160, -90))
            .xFromRight()
            .yFromBottom()

    val menuStorySkipYesClick = Location(320, 1100).xFromCenter()

    val retryRegion = Region(20, 1000, 700, 300).xFromCenter()

    val staminaScreenRegion = Region(-680, 200, 300, 300).xFromCenter()
    val staminaOkClick = Location(370, 1120).xFromCenter()

    val withdrawRegion = Region(-880, 540, 1800, 190).xFromCenter()
    val withdrawAcceptClick = Location(485, 720).xFromCenter()
    val withdrawCloseClick = Location(-10, 1140).xFromCenter()

    val supportScreenRegion =
        if (isWide)
            Region(150, 0, 200, 400)
        else Region(0, 0, 200, 400)

    val supportExtraRegion =
        if (isWide)
            Region(1380, 200, 130, 130)
        else Region(1200, 200, 130, 130)

    val supportUpdateClick =
        if (isWide)
            Location(1870, 260)
        else Location(1670, 250)

    val supportListTopClick =
        (if (isWide)
            Location(-218, 360)
        else Location(-80, 360)).xFromRight()

    val supportFirstSupportClick = Location(0, 500).xFromCenter()

    val supportUpdateYesClick = Location(200, 1110).xFromCenter()

    // Support Screen offset
    // For wide-screen: centered in this region: 305 left to 270 right
    // For 16:9 - 94 left to 145 right
    val supportOffset =
        if (isWide) {
            val width = 2560 - 94 - 145
            val total = scriptArea.Width - 305 - 270
            val border = ((total - width) / 2.0).roundToInt()

            Location(305 + border, 0)
        } else Location(94, 0)

    val supportListRegion = Region(-24, 332, 378, 1091) + supportOffset

    val supportFriendRegion = Region(
        2140,
        supportListRegion.Y,
        120,
        supportListRegion.Height
    ) + supportOffset

    val supportFriendsRegion = Region(354, 332, 1210, 1091) + supportOffset

    val supportMaxAscendedRegion = Region(282, 0, 16, 120) + supportOffset
    val supportLimitBreakRegion = Region(282, 0, 16, 90) + supportOffset

    val supportRegionToolSearchRegion = Region(2006, 0, 370, 1440) + supportOffset
    val supportDefaultBounds = Region(-18, 0, 2356, 428) + supportOffset
    val supportDefaultCeBounds = Region(-18, 270, 378, 150) + supportOffset

    fun locate(refillResource: RefillResourceEnum) = when (refillResource) {
        RefillResourceEnum.Bronze -> 1140
        RefillResourceEnum.Silver -> 922
        RefillResourceEnum.Gold -> 634
        RefillResourceEnum.SQ -> 345
    }.let { y -> Location(-530, y).xFromCenter() }

    fun locate(boost: BoostItem.Enabled) = when (boost) {
        BoostItem.Enabled.Skip -> Location(1652, 1304)
        BoostItem.Enabled.BoostItem1 -> Location(1280, 418)
        BoostItem.Enabled.BoostItem2 -> Location(1280, 726)
        BoostItem.Enabled.BoostItem3 -> Location(1280, 1000)
    }.xFromCenter()

    fun locate(orderChangeMember: OrderChangeMember) = when (orderChangeMember) {
        OrderChangeMember.Starting.A -> Location(-1000, 700)
        OrderChangeMember.Starting.B -> Location(-600, 700)
        OrderChangeMember.Starting.C -> Location(-200, 700)
        OrderChangeMember.Sub.A -> Location(200, 700)
        OrderChangeMember.Sub.B -> Location(600, 700)
        OrderChangeMember.Sub.C -> Location(1000, 700)
    }.xFromCenter()

    fun locate(servantTarget: ServantTarget) = when (servantTarget) {
        ServantTarget.A -> Location(-580, 880)
        ServantTarget.B -> Location(0, 880)
        ServantTarget.C -> Location(660, 880)
        ServantTarget.Left -> Location(-290, 880)
        ServantTarget.Right -> Location(330, 880)
    }.xFromCenter()

    fun locate(skill: Skill.Servant) = when (skill) {
        Skill.Servant.A1 -> Location(140, 1155)
        Skill.Servant.A2 -> Location(328, 1155)
        Skill.Servant.A3 -> Location(514, 1155)
        Skill.Servant.B1 -> Location(775, 1155)
        Skill.Servant.B2 -> Location(963, 1155)
        Skill.Servant.B3 -> Location(1150, 1155)
        Skill.Servant.C1 -> Location(1413, 1155)
        Skill.Servant.C2 -> Location(1600, 1155)
        Skill.Servant.C3 -> Location(1788, 1155)
    } + Location(if (isWide) 108 else 0, if (isWide) -22 else 0)

    fun locate(skill: Skill.Master) = when (skill) {
        Skill.Master.A -> Location(-740, 620)
        Skill.Master.B -> Location(-560, 620)
        Skill.Master.C -> Location(-400, 620)
    }.xFromRight() + Location(if (isWide) -120 else 0, 0)

    fun locate(skill: Skill) = when (skill) {
        is Skill.Servant -> locate(skill)
        is Skill.Master -> locate(skill)
    }

    fun locate(enemy: EnemyTarget) = when (enemy) {
        EnemyTarget.A -> Location(90, 80)
        EnemyTarget.B -> Location(570, 80)
        EnemyTarget.C -> Location(1050, 80)
    } + Location(if (isWide) 183 else 0, 0)

    fun dangerRegion(enemy: EnemyTarget) = when (enemy) {
        EnemyTarget.A -> Region(0, 0, 485, 220)
        EnemyTarget.B -> Region(485, 0, 482, 220)
        EnemyTarget.C -> Region(967, 0, 476, 220)
    } + Location(if (isWide) 150 else 0, 0)

    val selectedPartyRegion = Region(-270, 62, 550, 72).xFromCenter()
    val partySelectionArray = (0..9).map {
        // Party indicators are center-aligned
        val x = ((it - 4.5) * 50).roundToInt()

        Location(x, 100).xFromCenter()
    }

    val battleStageCountRegion
        get() = when (prefs.gameServer) {
            GameServerEnum.Tw -> Region(-850, 25, 55, 60)
            GameServerEnum.Jp -> {
                if (isWide)
                    Region(-836, 23, 33, 53)
                else Region(-796, 28, 31, 44)
            }
            else -> Region(-838, 25, 46, 53)
        }.xFromRight()

    val battleScreenRegion =
        (if (isWide)
            Region(-660, -210, 400, 175)
        else Region(-455, -181, 336, 116))
            .xFromRight()
            .yFromBottom()

    val battleAttackClick =
        (if (isWide)
            Location(-460, -230)
        else Location(-260, -240))
            .xFromRight()
            .yFromBottom()

    val battleMasterSkillOpenClick =
        (if (isWide)
            Location(-300, 640)
        else Location(-180, 640))
            .xFromRight()

    val battleSkillOkClick = Location(400, 850).xFromCenter()
    val battleOrderChangeOkClick = Location(0, 1260).xFromCenter()
    val battleExtraInfoWindowCloseClick = Location(-10, 10).xFromRight()

    val resultFriendRequestRegion = Region(600, 150, 100, 94).xFromCenter()
    val resultFriendRequestRejectClick = Location(-680, 1200).xFromCenter()

    val fpSummonCheck = Region(100, 1220, 75, 75).xFromCenter()
    val fpContinueSummonRegion = Region(-36, 1264, 580, 170).xFromCenter()
    val fpFirst10SummonClick = Location(120, 1120).xFromCenter()
    val fpOkClick = Location(320, 1120).xFromCenter()
    val fpContinueSummonClick = Location(320, 1325).xFromCenter()
    val fpSkipRapidClick = Location(1240, 1400).xFromCenter()
}
