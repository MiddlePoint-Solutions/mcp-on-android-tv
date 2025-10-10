package io.middlepoint.mcponandroid.mcp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.touchlab.kermit.Logger
import io.middlepoint.mcponandroid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class McpService : Service() {

  private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private lateinit var mcpServer: McpServer
  private val logger = Logger.withTag("McpService")
  private var isForeground = false

  companion object {
    private const val NOTIFICATION_ID = 1
    private const val CHANNEL_ID = "McpServiceChannel"
  }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    initMcpServer()
    logger.d("McpService created")
  }

  private fun initMcpServer() {
    mcpServer = McpServer.getInstance(applicationContext)
    serverScope.launch {
      mcpServer.runSseMcpServerUsingKtorPlugin(8081)
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (!isForeground) {
      val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("MCP Server running")
        .setSmallIcon(R.mipmap.ic_launcher)
        .build()

      startForeground(NOTIFICATION_ID, notification)
      isForeground = true
    }
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    isForeground = false
    logger.d("McpService destroyed")
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun createNotificationChannel() {
    val serviceChannel = NotificationChannel(
      CHANNEL_ID,
      "MCP Service Channel",
      NotificationManager.IMPORTANCE_DEFAULT
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(serviceChannel)
  }

}
