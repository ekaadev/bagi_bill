package com.bagi_bill.bagi_bill.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/**
 * Reusable confirmation dialog for draft saving
 * Shows when user tries to exit to Home before completing the flow
 */
@Composable
fun DraftConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSaveDraft: () -> Unit,
    onDiscard: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Simpan sebagai draft?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Data yang sudah diinput akan disimpan sebagai draft dan bisa dilanjutkan nanti."
                )
            },
            confirmButton = {
                TextButton(onClick = onSaveDraft) {
                    Text(text = "Simpan Draft")
                }
            },
            dismissButton = {
                TextButton(onClick = onDiscard) {
                    Text(text = "Buang")
                }
            }
        )
    }
}
