package com.jizhang.smartledger.presentation

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jizhang.smartledger.di.AppContainer
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import java.text.SimpleDateFormat
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import java.util.Locale

/** Root Compose application that renders the SmartLedger MVP. */
@Composable
fun SmartLedgerApp(container: AppContainer) {
    val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SmartLedgerTheme {
        MainScreen(uiState, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    uiState: AppUiState,
    actions: AppViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var editingDraft by remember { mutableStateOf<TransactionDraft?>(null) }
    LaunchedEffect(uiState.message) {
        showMessage(uiState.message, snackbarHostState, actions)
    }
    editingDraft?.let { draft ->
        EditDraftDialog(
            draft = draft,
            categories = uiState.categoryOptions,
            onDismiss = { editingDraft = null },
            onConfirm = { input ->
                actions.confirm(draft.id, input)
                editingDraft = null
            }
        )
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("SmartLedger") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DashboardHeader(uiState.todayExpense, uiState.pendingDrafts.size) }
            item { CaptureActions(actions) }
            item { RefreshAction(actions) }
            item { SectionTitle("待确认") }
            items(uiState.pendingDrafts, key = { it.id }) { draft ->
                DraftRow(draft, actions, onEdit = { editingDraft = draft })
            }
            item { SectionTitle("最近账单") }
            items(uiState.recentTransactions, key = { it.id }) { transaction ->
                TransactionRow(transaction)
            }
            item { SettingsPanel(uiState, actions) }
        }
    }
}

private suspend fun showMessage(
    message: String?,
    snackbarHostState: SnackbarHostState,
    actions: AppViewModel
) {
    if (message == null) {
        return
    }
    snackbarHostState.showSnackbar(message)
    actions.clearMessage()
}

@Composable
private fun DashboardHeader(
    todayExpense: Money,
    pendingCount: Int
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text("今日支出", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "¥${todayExpense.format()}",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text("待确认 $pendingCount 笔", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CaptureActions(actions: AppViewModel) {
    val context = LocalContext.current
    var manualText by remember { mutableStateOf("") }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uri -> uri?.let { actions.importImage(it.toString()) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { imagePicker.launch("image/*") }) {
                Icon(Icons.Outlined.ImageSearch, contentDescription = null)
                Text("截图识别", modifier = Modifier.padding(start = 6.dp))
            }
            OutlinedButton(onClick = { openNotificationSettings(context) }) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
                Text("通知权限", modifier = Modifier.padding(start = 6.dp))
            }
        }
        OutlinedTextField(
            value = manualText,
            onValueChange = { manualText = it },
            label = { Text("粘贴支付通知或账单文字") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(onClick = {
            actions.submitManualText(manualText)
            manualText = ""
        }) {
            Icon(Icons.Outlined.TextSnippet, contentDescription = null)
            Text("识别文本", modifier = Modifier.padding(start = 6.dp))
        }
    }
}

@Composable
private fun RefreshAction(actions: AppViewModel) {
    OutlinedButton(onClick = actions::refresh) {
        Icon(Icons.Outlined.Refresh, contentDescription = null)
        Text("刷新后端数据", modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun DraftRow(
    draft: TransactionDraft,
    actions: AppViewModel,
    onEdit: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(draft.merchant, fontWeight = FontWeight.SemiBold)
                Text("${draft.type.label()} ¥${draft.money.format()} · ${dateText(draft.paidAt)}")
                Text("置信度 ${(draft.confidence * 100).toInt()}%")
            }
            IconButton(onClick = { actions.confirm(draft) }) {
                Icon(Icons.Outlined.Check, contentDescription = "确认入账")
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, contentDescription = "修改")
            }
            IconButton(onClick = { actions.ignore(draft.id) }) {
                Icon(Icons.Outlined.Close, contentDescription = "忽略")
            }
        }
    }
}

@Composable
private fun EditDraftDialog(
    draft: TransactionDraft,
    categories: List<CategoryOption>,
    onDismiss: () -> Unit,
    onConfirm: (ConfirmedDraftInput) -> Unit
) {
    var amountText by remember { mutableStateOf(draft.money.format()) }
    var merchantText by remember { mutableStateOf(draft.merchant) }
    var categoryId by remember { mutableStateOf(draft.categoryId ?: categories.firstOrNull()?.id ?: 1L) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改后入账") },
        text = {
            EditDraftFields(amountText, merchantText, categoryId, categories) { amount, merchant, id ->
                amountText = amount
                merchantText = merchant
                categoryId = id
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(buildInput(draft, amountText, merchantText, categoryId)) }) {
                Text("保存")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun EditDraftFields(
    amountText: String,
    merchantText: String,
    categoryId: Long,
    categories: List<CategoryOption>,
    onChange: (String, String, Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { onChange(it, merchantText, categoryId) },
            label = { Text("金额") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = merchantText,
            onValueChange = { onChange(amountText, it, categoryId) },
            label = { Text("商户") }
        )
        CategoryDropdown(categoryId, categories) { onChange(amountText, merchantText, it) }
    }
}

@Composable
private fun CategoryDropdown(
    categoryId: Long,
    categories: List<CategoryOption>,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.firstOrNull { it.id == categoryId }?.name ?: "其他"
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedName)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { option ->
                DropdownMenuItem(text = { Text(option.name) }, onClick = {
                    onSelected(option.id)
                    expanded = false
                })
            }
        }
    }
}

private fun buildInput(
    draft: TransactionDraft,
    amountText: String,
    merchantText: String,
    categoryId: Long
): ConfirmedDraftInput {
    return ConfirmedDraftInput(
        amountCents = amountText.toCentsOrDefault(draft.money.amountCents),
        type = draft.type,
        merchant = merchantText.ifBlank { draft.merchant },
        categoryId = categoryId,
        paidAt = draft.paidAt
    )
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.merchant, fontWeight = FontWeight.SemiBold)
                Text("${transaction.type.label()} · ${dateText(transaction.paidAt)}")
            }
            Text("¥${transaction.money.format()}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingsPanel(
    uiState: AppUiState,
    actions: AppViewModel
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SectionTitle("设置")
        SettingSwitch(
            title = "实验屏幕识别",
            checked = uiState.settings.screenCaptureEnabled,
            onChange = actions::setScreenCaptureEnabled
        )
        SettingSwitch(
            title = "实验无障碍识别",
            checked = uiState.settings.accessibilityCaptureEnabled,
            onChange = actions::setAccessibilityEnabled
        )
        Text(
            text = "实验能力默认关闭，仅在你明确授权后作为辅助入口。",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.SettingsApplications, contentDescription = null)
        Text(title, modifier = Modifier.weight(1f).padding(start = 8.dp))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SmartLedgerTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

private fun openNotificationSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    context.startActivity(intent)
}

private fun dateText(epochMillis: Long): String {
    val formatter = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
    return formatter.format(Date(epochMillis))
}

private fun String.toCentsOrDefault(defaultValue: Long): Long {
    return runCatching {
        BigDecimal(trim()).movePointRight(2).setScale(0, RoundingMode.HALF_UP).toLong()
    }.getOrDefault(defaultValue)
}
