package com.lmw.watermonitorandroid.domain.system.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.successMessage) {
        val msg = state.errorMessage ?: state.successMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    if (state.isEditing) {
        ServerAddressDialog(
            ip = state.editIp,
            port = state.editPort,
            isSaving = state.isSaving,
            onIpChange = { viewModel.updateEditIp(it) },
            onPortChange = { viewModel.updateEditPort(it) },
            onConfirm = { viewModel.saveServerAddress() },
            onDismiss = { viewModel.cancelEditing() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("系统设置") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServerAddressCard(
                address = state.serverAddress,
                onEdit = { viewModel.startEditing() }
            )

            ConnectionTestCard(
                isTesting = state.isTesting,
                testResult = state.testResult,
                onTest = { viewModel.testConnection() }
            )

            DeviceInfoCard(
                deviceInfo = state.deviceInfo,
                isLoading = state.isLoadingInfo,
                onRefresh = { viewModel.loadDeviceInfo() }
            )

            AboutCard()
        }
    }
}

@Composable
private fun ServerAddressCard(
    address: com.lmw.watermonitorandroid.domain.system.api.ServerAddress,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "服务器地址",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(label = "IP 地址", value = address.ip)
            InfoRow(label = "端口", value = address.port.toString())
            InfoRow(label = "SSE 端点", value = address.sseUrl)

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("修改地址")
            }
        }
    }
}

@Composable
private fun ConnectionTestCard(
    isTesting: Boolean,
    testResult: Boolean?,
    onTest: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "连接测试",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isTesting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("测试中...", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (testResult != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (testResult) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (testResult) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (testResult) "连接成功" else "连接失败",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (testResult) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
            } else {
                Text(
                    text = "点击下方按钮测试与设备的连接",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onTest,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isTesting
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("测试连接")
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(
    deviceInfo: com.lmw.watermonitorandroid.domain.system.api.DeviceInfo,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "设备信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (deviceInfo.firmwareVersion.isEmpty() && !isLoading) {
                Text(
                    text = "需要设备端支持 /api/info 接口",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                InfoRow(label = "固件版本", value = deviceInfo.firmwareVersion.ifEmpty { "-" })
                InfoRow(label = "运行时间", value = formatUptime(deviceInfo.uptime))
                InfoRow(label = "信号强度", value = "${deviceInfo.signalStrength} dBm")
                InfoRow(label = "WiFi SSID", value = deviceInfo.ssid.ifEmpty { "-" })
                InfoRow(label = "IP 地址", value = deviceInfo.ip.ifEmpty { "-" })
                InfoRow(label = "MAC 地址", value = deviceInfo.macAddress.ifEmpty { "-" })
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("刷新")
            }
        }
    }
}

@Composable
private fun AboutCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(label = "应用名称", value = "WaterMonitor")
            InfoRow(label = "版本", value = "1.0.0")
            InfoRow(label = "目标设备", value = "ESP-12F + STM32")
            InfoRow(label = "通信协议", value = "SSE + HTTP REST")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ServerAddressDialog(
    ip: String,
    port: String,
    isSaving: Boolean,
    onIpChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("修改服务器地址") },
        text = {
            Column {
                OutlinedTextField(
                    value = ip,
                    onValueChange = onIpChange,
                    label = { Text("IP 地址") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = onPortChange,
                    label = { Text("端口") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSaving && ip.isNotBlank() && port.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(16.dp).height(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("保存")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("取消")
            }
        }
    )
}

private fun formatUptime(seconds: Long): String {
    if (seconds <= 0) return "-"
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val mins = (seconds % 3600) / 60
    return buildString {
        if (days > 0) append("${days}天 ")
        if (hours > 0) append("${hours}小时 ")
        append("${mins}分钟")
    }
}