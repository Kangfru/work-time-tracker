package data.notification

import domain.model.TimeRemaining
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

class NotificationService {
    private var trayIcon: TrayIcon? = null
    private var onTrayIconClick: (() -> Unit)? = null
    
    fun initialize(onIconClick: () -> Unit) {
        onTrayIconClick = onIconClick
        
        if (SystemTray.isSupported()) {
            SwingUtilities.invokeLater {
                try {
                    val systemTray = SystemTray.getSystemTray()
                    val image = createTrayIcon()
                    
                    trayIcon = TrayIcon(image, "퇴근시간 추적기").apply {
                        isImageAutoSize = true
                        
                        popupMenu = PopupMenu().apply {
                            add(MenuItem("퇴근시간 추적기").apply {
                                isEnabled = false
                            })
                            addSeparator()
                            add(MenuItem("창 보이기").apply {
                                addActionListener {
                                    onTrayIconClick?.invoke()
                                }
                            })
                            add(MenuItem("종료").apply {
                                addActionListener {
                                    System.exit(0)
                                }
                            })
                        }
                        
                        // 트레이 아이콘 클릭 시 창 앞으로 가져오기
                        addMouseListener(object : MouseAdapter() {
                            override fun mouseClicked(e: MouseEvent) {
                                if (e.button == MouseEvent.BUTTON1) { // 좌클릭
                                    onTrayIconClick?.invoke()
                                }
                            }
                        })
                    }
                    
                    systemTray.add(trayIcon)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    fun updateQuickInfo(
        timeRemaining: TimeRemaining,
        earnedMoney: Long,
        expectedEndTime: String
    ) {
        SwingUtilities.invokeLater {
            val info = buildString {
                append("⏰ 퇴근까지: ${timeRemaining.toFormattedString()}")
                append("\n")
                append("🕐 예상 퇴근: $expectedEndTime")
                append("\n")
                append("💰 오늘 번 돈: ${formatMoney(earnedMoney)}")
            }
            
            trayIcon?.toolTip = info
        }
    }
    
    fun sendNotification(title: String, message: String) {
        SwingUtilities.invokeLater {
            trayIcon?.displayMessage(
                title,
                message,
                TrayIcon.MessageType.INFO
            )
        }
    }
    
    private fun createTrayIcon(): Image {
        val size = 32
        val bufferedImage = java.awt.image.BufferedImage(
            size, 
            size, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        )
        val g = bufferedImage.createGraphics()
        
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        
        g.color = Color(100, 150, 255)
        g.fillOval(2, 2, size - 4, size - 4)
        
        g.color = Color.WHITE
        val roofX = intArrayOf(size/2, size/4, 3*size/4)
        val roofY = intArrayOf(size/4, size/2, size/2)
        g.fillPolygon(roofX, roofY, 3)
        g.fillRect(size/3, size/2, size/3, size/4)
        
        g.dispose()
        return bufferedImage
    }
    
    private fun formatMoney(amount: Long): String {
        return "%,d원".format(amount)
    }
    
    fun dispose() {
        SwingUtilities.invokeLater {
            trayIcon?.let {
                try {
                    SystemTray.getSystemTray().remove(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
