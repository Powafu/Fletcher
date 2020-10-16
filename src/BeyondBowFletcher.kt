package scripts

import org.tribot.api.Timing
import org.tribot.api2007.*
import org.tribot.api2007.Skills.SKILLS
import org.tribot.script.Script
import org.tribot.script.ScriptManifest
import org.tribot.script.interfaces.Painting
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

@Suppress("NAME_SHADOWING")
@ScriptManifest(authors = ["IM4EVER12C", "Powa"], category = "Fletching", name = "Beyond Bows")
class BeyondBowFletcher : Script(), Painting {
    private val startTime = System.currentTimeMillis()
    private var fletchingLevelBefore = 0
    private var fletchingExpBefore = 0
    private var amountCut = 0
    private var logCounter = 0
    private var afkTicks = 0
    private var maxAFKTicks = 25
    private var temp: String = null.toString()
    private var tempMod: String = null.toString()
    private val fletchingLvl = Skills.getActualLevel(SKILLS.FLETCHING)
    private val log = arrayOf("Logs", "Oak Logs", "Willow logs", "Maple logs", "Yew logs", "Magic Logs")

    companion object FletchingLvlCheck {
        var returnLogLvl = LogType.values().associateBy(LogType::loglvl)
        fun fromInt(type: Int) = returnLogLvl
    }

    private enum class BowToFletch {
        ARROW_SHAFT,
        LONGBOW,
        OAK_SHORTBOW,
        OAK_LONGBOW,
        WILLOW_SHORTBOW,
        WILLOW_LONGBOW,
        MAPLE_SHORTBOW,
        MAPLE_LONGBOW,
        YEW_SHORTBOW,
        YEW_LONGBOW,
        MAGIC_SHORTBOW,
        MAGIC_LONGBOW;
    }
    private var bowTypeLvl = arrayOf(
            Pair(85, 99),
            Pair(70, 79),
            Pair(50, 69),
            Pair(35, 49),
            Pair(25, 29),
            Pair(1, 19)
    )
    private enum class var bow = arrayOf("shortbow", "longbow", "arrow shafts")

    override fun onPaint(g: Graphics) {
        val timeRan = System.currentTimeMillis() - startTime
        val alpha = 127
        val alpha2 = 186
        val myColour = Color(255, 25, 25, alpha)
        val myColour2 = Color(1, 1, 125, alpha2)
        g.color = myColour
        g.fillRect(5, 244, 510, 90)
        g.color = myColour2
        g.font = Font("Calibri", Font.BOLD, 35)
        g.drawString("Beyond Bows V1.0", 140, 268)
        g.drawString("________________", 140, 268)
        g.font = Font("Calibri", Font.BOLD, 19)
        g.color = Color.yellow
        g.drawString("| Bows Fletched: $amountCut", 10, 300)
        g.drawString("| Current Cut: " + currentCut(), 10, 315)
        g.drawString("| Fletching Level: " + SKILLS.FLETCHING.actualLevel + " ("
                + (SKILLS.FLETCHING.actualLevel - fletchingLevelBefore) + ")", 10, 330)
        g.drawString("| Experience Gained: " + gainedXp(), 245, 300)
        g.font = Font("Calibri", Font.BOLD, 12)
        g.color = Color.WHITE
        g.drawString("Time Elapsed: " + Timing.msToString(timeRan), 215, 284)
    }

    override fun run() {
        if (fletchingLevelBefore < 1) {
            fletchingLevelBefore = SKILLS.FLETCHING.actualLevel
        }
        while (afkTicks < maxAFKTicks)  {
            sleep(fletch())
        }
    }

    private fun currentCut(): String {
        if (logType() === log[0]) {
            temp = bowType()
        } else if (logType().toLowerCase().contains("logs")
                && bowType() != bow[2]) {
            tempMod = logType()
            temp = tempMod.replace("logs", "") + " " + bowType()
        } else if (bowType().toLowerCase().contains("arrow shafts")) {
            temp = "arrow shafts"
        } else {
            temp = logType() + bowType()
        }
        return if (!temp.contains("arrow shafts")) {
            "$temp (u)"
        } else {
            temp
        }
    }

    private fun getTotalFletched() {
        if (logCounter == 0) {
            logCounter = Inventory.getCount(logType())
        } else if (Inventory.getCount(logType()) < logCounter) {
            amountCut += 1
            logCounter = Inventory.getCount(logType())
        }

    }

    private fun gainedXp(): Int {
        if (fletchingExpBefore < 1) {
            fletchingExpBefore = SKILLS.FLETCHING.xp
        }
        return SKILLS.FLETCHING.xp - fletchingExpBefore
    }

    private fun bowType(): String {
        val z: Int = fletchingLvl
        val flc = enumValues<LogType>()
        var dfl = 0
        if (FletchingLvlCheck.returnLogLvl.contains(z)) {
            return LogType.toString()
        }


    }

    private fun logType(): String {
        if (fletchingLvl < 10) {
            return log[0]
        } else {
            for (n in logLvl[0]) if (n.equals(fletchingLvl)) {
                return log[1]
            } else {
                for (n in logLvl[1]) if (n.equals(fletchingLvl)) {
                    return log[2]
                } else {
                    for (n in logLvl[3]) if (n.equals(fletchingLvl)) {
                        return log[4]
                    } else {
                        for (n in logLvl[4]) if (n.equals(fletchingLvl)) {
                            break
                        }
                    }
                }
            }
        }
        return log[5]
    }

    private fun bankProcess() {
        if (Inventory.getCount(logType()) < 1) {
            if (!Banking.isBankLoaded()) {
                Banking.openBankBanker()
            } else {
                Banking.depositAllExcept("Knife")
                if (Inventory.getCount(logType()) < 1) {
                    Banking.withdraw(27, logType())
                } else if (Inventory.getCount("Knife") < 1) {
                    Banking.withdraw(1, "Knife")
                }
            }
        } else {
            if (Banking.isBankLoaded()) {
                Banking.close()
            }
        }
    }

    private fun cutting() {
        if (Inventory.getCount(logType()) >= 1 && Inventory.getCount("Knife") >= 1) {
            if (Banking.isBankLoaded()) {
                Banking.close()
            }
            val myKnife = Inventory.find("Knife")
            val myLogType = Inventory.find(logType())
            if (Player.getAnimation() == -1) {
                if (!Game.getUptext().contains("Knife ->")) {
                    myKnife[0].click("Use")
                }

                if (Game.getUptext().contains("Knife ->")) {
                    val nxt: Int
                    val amt: Int = Inventory.getCount(logType())
                    nxt = (Math.random() * (amt - 0)).toInt()
                    myLogType[nxt].click()
                }
                if (Interfaces.isInterfaceValid(270) && Interfaces.isInterfaceSubstantiated(270, 16)) {
                    val rsInterfaceChildOption1 = Interfaces.get(270, 14)
                    val rsInterfaceChildOption2 = Interfaces.get(270, 15)
                    val rsInterfaceChildOption3 = Interfaces.get(270, 16)
                    val rsInterfaceChildOption4 = Interfaces.get(270, 17)
                    val rsInterfaceChildOption5 = Interfaces.get(270, 18)
                    when {
                        rsInterfaceChildOption1.componentName.toLowerCase().contains(bowType()) -> {
                            rsInterfaceChildOption1.click()
                        }
                        rsInterfaceChildOption2.componentName.toLowerCase().contains(bowType()) -> {
                            rsInterfaceChildOption2.click()
                        }
                        rsInterfaceChildOption3.componentName.toLowerCase().contains(bowType()) -> {
                            rsInterfaceChildOption3.click()
                        }
                        rsInterfaceChildOption4.componentName.toLowerCase().contains(bowType()) -> {
                            rsInterfaceChildOption4.click()
                        }
                        rsInterfaceChildOption5.componentName.toLowerCase().contains(bowType()) -> {
                            rsInterfaceChildOption5.click()
                        }
                    }
                    sleep(1000)
                }
            }
        } else {
            bankProcess()
        }
    }
    /*   private fun errorMessage() {
           if (Inventory.getCount(logType()) < 1) {
               println("No logs to fletch. Stopping script.")
           } else if (Inventory.getCount("Knife") < 1) {
               println("No knife found. Stopping script.")
           }
           else if (afkTicks == maxAFKTicks) {
               println("Max AFK time reached. Stopping script.");
           }

       }
   */
    private fun fletch(): Long {
        getTotalFletched()
        cutting()
        afkTicks += 1
        return 25
    }
}



