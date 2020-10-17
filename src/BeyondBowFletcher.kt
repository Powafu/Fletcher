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
@ScriptManifest(authors = ["IM4EVER12C", "Powa"], category = "Fletching", name = "Beyond Bows 2.0")
class BeyondBowFletcher : Script(), Painting {
    private val startTime = System.currentTimeMillis()
    private var fletchingLevelBefore = 0
    private var fletchingExpBefore = 0
    private var amountCut = 0
    private var logCounter = 0
    private var afkTicks = 0
    private var maxAFKTicks = 25
    private val fletchingLvl = Skills.getActualLevel(SKILLS.FLETCHING)
    private val log: MutableMap<IntRange, String> = mutableMapOf(0..19 to "Logs", 20..34 to "Oak Logs", 35..49 to "Willow logs", 50..69 to "Maple logs", 70..84 to "Yew logs", 85..99 to "Magic Logs").withDefault { "Logs" }
    private val bow: MutableMap<IntRange, String> = mutableMapOf(0..9 to "Arrow shafts", 10..19 to "Longbow", 20..24 to "Oak shortbow", 25..34 to "Oak longbow", 35..39 to "Willow shortbow", 40..49 to "Willow longbow", 50..54 to "Maple shortbow", 55..69 to "Maple longbow", 70..84 to "Yew longbow", 85..99 to "Magic longbow").withDefault { "Arrow shaft" }

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
        g.drawString("| Current Cut: " + bowType(), 10, 315)
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
        errorMessage()
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

    private fun logType(test: Boolean = log.containsKey(fletchingLvl)): String {
        return log.get(fletchingLvl).toString()
    }


    private fun bowType(test: Boolean = bow.containsKey(fletchingLvl)): String {
        return bow.get(fletchingLvl).toString()
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

    private fun errorMessage() {
        when {
            Inventory.getCount(logType()) < 1 -> {
                println("No logs to fletch. Stopping script.")
            }
            Inventory.getCount("Knife") < 1 -> {
                println("No knife found. Stopping script.")
            }
            afkTicks == maxAFKTicks -> {
                println("Max AFK time reached. Stopping script.")
            }
        }

       }

    private fun fletch(): Long {
        getTotalFletched()
        cutting()
        afkTicks += 1
        return 25
    }
}



