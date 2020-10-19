package scripts

import org.tribot.api.Timing
import org.tribot.api2007.*
import org.tribot.api2007.Interfaces
import org.tribot.api2007.Inventory
import org.tribot.api2007.Skills.SKILLS
import org.tribot.script.Script
import org.tribot.script.ScriptManifest
import org.tribot.script.interfaces.Painting
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.lang.Math.random
@ScriptManifest(authors = ["Powa", "IM4EVER12C"], category = "Fletching", name = "Powagressive Open Source Fletcher")
class BeyondBowFletcher : Script(), Painting {
    private var shouldRun = true
    private val startTime = System.currentTimeMillis()
    private var fletchingLevelBefore = 0
    private var fletchingExpBefore = 0
    private var amountCut = 0
    private var logCounter = 0
    private val knife = "Knife"
    private val knifeA = "Knife ->"
    private var hasClickedLogs : Boolean = false
    private var hasClickedKnife :Boolean = false
    private var rnds = (850..1337)
    private var rand = rnds.random().toLong()
    private val fLvl = Skills.getActualLevel(SKILLS.FLETCHING)
    private var log = arrayOf(
            "Logs",
            "Oak Logs",
            "Willow logs",
            "Maple logs",
            "Yew logs",
            "Magic logs"
    )

    private var bowToFletch = arrayListOf(
            "Arrow shaft",
            "Longbow",
            "Oak shortbow",
            "Oak longbow",
            "Willow shortbow",
            "Willow longbow",
            "Maple shortbow",
            "Maple longbow",
            "Yew longbow",
            "Magic longbow"
    )

    override fun onPaint(g: Graphics) {
        val timeRan = System.currentTimeMillis() - startTime
        val alpha = 127
        val alpha2 = 186
        val myColour = Color(150, 150, 150, alpha)
        val myColour2 = Color(1, 1, 125, alpha2)
        g.color = myColour
        g.fillRect(5, 250, 510, 90)
        g.color = myColour2
        g.font = Font("Calibri", Font.BOLD, 24)
        g.drawString("Powagressive Open Source Fletcher", 20, 280)
        g.font = Font("Calibri", Font.BOLD, 16)
        g.color = Color.yellow
        g.drawString("| Bows Fletched: $amountCut", 280, 315)
        g.drawString("| Current Cut: " + bowType(), 10, 315)
        g.drawString("| Fletching Level: " + SKILLS.FLETCHING.actualLevel + " ("
                + (SKILLS.FLETCHING.actualLevel - fletchingLevelBefore) + ")", 10, 300)
        g.drawString("| Experience Gained: " + gainedXp(), 280, 300)
        g.drawString("| Time Elapsed: " + Timing.msToString(timeRan), 10, 330)
    }

    override fun run() {
        if (fletchingLevelBefore < 1) {
            fletchingLevelBefore = SKILLS.FLETCHING.actualLevel
        }
        while (shouldRun) {
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

    private fun bowType(): String {
        return when (fLvl) {
            in 0..9 -> bowToFletch[0]
            in 10..19 -> bowToFletch[1]
            in 20..24 -> bowToFletch[2]
            in 25..29 -> bowToFletch[3]
            in 30..34 -> bowToFletch[4]
            in 35..49 -> bowToFletch[5]
            in 50..54 -> bowToFletch[6]
            in 55..69 -> bowToFletch[7]
            in 70..84 -> bowToFletch[8]
            in 85..99 -> bowToFletch[9]
            else -> "You're not in runescape anymore"
        }

    }

    private fun logType(): String {
        return when (fLvl) {
            in 0..19 -> log[0]
            in 20..29 -> log[1]
            in 30..49 -> log[2]
            in 50..69 -> log[3]
            in 70..84 -> log[4]
            in 84..99 -> log[5]
            else -> "Kansas"
        }
    }
    private fun resetClicks() {
        hasClickedLogs = false
        hasClickedKnife = false
    }

    private fun bankProcess() {
        if (Inventory.getCount(logType()) < 1) {
            if (!Banking.isBankLoaded()) {
                Banking.openBankBanker()
            } else {
                Banking.depositAllExcept(knife)
                if ((!hasClickedLogs) && (Inventory.getCount(logType())) < 1) {
                    Banking.withdraw(27, logType())
                    hasClickedLogs = true
                }
                if ((!hasClickedKnife) && (Inventory.getCount(knife) < 1)) {
                    Banking.withdraw(1, "Knife")
                    hasClickedKnife = true

                }
            }
        }
        else if (!Banking.isBankLoaded()){
            Banking.close()
            resetClicks()
        }
    }

    private fun cutting() {
        if (Inventory.getCount(logType()) >= 1 && Inventory.getCount(knife) >= 1) {
            resetClicks()
            if (Banking.isBankLoaded()) {
                Banking.close()
            }
            val myKnife = Inventory.find(knife)
            val myLogType = Inventory.find(logType())
            if (Player.getAnimation() == -1) {
                if (!hasClickedKnife) {
                    if (knifeA !in Game.getUptext()) {
                        myKnife[0].click("Use")
                        hasClickedKnife = true
                    }
                }
                if (!hasClickedLogs) {
                    if (knifeA in Game.getUptext()) {
                        val nxt: Int
                        val amt: Int = Inventory.getCount(logType())
                        nxt = (random() * (amt - 0)).toInt()
                        myLogType[nxt].click()
                        hasClickedLogs = true
                    }
                }
                if (Interfaces.isInterfaceValid(270) && Interfaces.isInterfaceSubstantiated(270, 16)) {
                    val rsInterfaceChildOption1 = Interfaces.get(270, 14)
                    val rsInterfaceChildOption2 = Interfaces.get(270, 15)
                    val rsInterfaceChildOption3 = Interfaces.get(270, 16)
                    val rsInterfaceChildOption4 = Interfaces.get(270, 17)
                    val rsInterfaceChildOption5 = Interfaces.get(270, 18)
                    when {
                        bowType() in rsInterfaceChildOption1.componentName -> rsInterfaceChildOption1.click()
                        bowType() in rsInterfaceChildOption2.componentName -> rsInterfaceChildOption2.click()
                        bowType() in rsInterfaceChildOption3.componentName -> rsInterfaceChildOption3.click()
                        bowType() in rsInterfaceChildOption4.componentName -> rsInterfaceChildOption4.click()
                        bowType() in rsInterfaceChildOption5.componentName -> rsInterfaceChildOption5.click()
                        else -> errorMessage()
                    }
                    resetClicks()
                    sleep(1000)
                }
            }
        }    else {
                bankProcess()
        }
    }

    private fun errorMessage() {
        shouldRun = when {
            Inventory.getCount(logType()) < 1 -> {
                println("No logs to fletch. Stopping script.")
                false
            }
            Inventory.getCount(knife) < 1 -> {
                println("No knife found. Stopping script.")
                false
            }
            else -> {
                println("Unknown Error")
                false
            }
        }
    }

    private fun fletch(): Long {
        getTotalFletched()
        cutting()
        return rand
    }
}



