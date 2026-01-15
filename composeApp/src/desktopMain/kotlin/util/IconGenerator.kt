package util

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object IconGenerator {
    fun generateIcon(outputPath: String) {
        // 1024x1024 크기로 생성 (macOS 아이콘 권장 크기)
        val size = 1024
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        
        // 안티앨리어싱 설정
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        
        // 배경 (연한 파란색 원)
        g.color = Color(100, 150, 255)
        g.fillOval(50, 50, size - 100, size - 100)
        
        // 이모지 그리기 (집 🏠)
        g.color = Color.WHITE
        g.font = Font("Apple Color Emoji", Font.PLAIN, 600)
        val emoji = "🏠"
        val metrics = g.fontMetrics
        val x = (size - metrics.stringWidth(emoji)) / 2
        val y = ((size - metrics.height) / 2) + metrics.ascent
        g.drawString(emoji, x, y)
        
        g.dispose()
        
        // PNG로 저장
        ImageIO.write(image, "png", File(outputPath))
        println("아이콘 생성 완료: $outputPath")
    }
}

fun main() {
    // 프로젝트 루트에 icon.png 생성
    IconGenerator.generateIcon("icon.png")
}
