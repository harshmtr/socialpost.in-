package com.example.socialpost.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import com.example.socialpost.R
import com.example.socialpost.data.model.PostStatus
import com.example.socialpost.ui.components.PostPreviewCard
import com.example.socialpost.ui.components.QualityMeter
import com.example.socialpost.ui.theme.LinkedInBlue
import com.example.socialpost.util.SharingUtils
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostCreatorScreen(
    viewModel: PostCreatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var showAdvancedSettings by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showImagePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.imageOptions) {
        if (uiState.imageOptions.isNotEmpty()) {
            showImagePicker = true
        }
    }

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSaveMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.title_post_drafting_engine),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.article?.title ?: stringResource(R.string.subtitle_custom_linkedin_post),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Source Article Banner
            uiState.article?.let { article ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LinkedInBlue.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = LinkedInBlue,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = article.source,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }

            // AI Prompt Configuration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = stringResource(R.string.cd_settings),
                                tint = LinkedInBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.label_post_parameters),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { showAdvancedSettings = !showAdvancedSettings }) {
                            Icon(
                                imageVector = if (showAdvancedSettings) Icons.Default.Tune else Icons.Default.Tune,
                                contentDescription = stringResource(R.string.cd_toggle_parameters),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hook Style Selection
                    Text(
                        text = stringResource(R.string.label_hook_style),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.hookStyles.forEach { hook ->
                            FilterChip(
                                selected = uiState.hookStyle == hook,
                                onClick = { viewModel.updateHookStyle(hook) },
                                label = { Text(hook, fontSize = 12.sp) },
                                modifier = Modifier.testTag("hook_chip_$hook")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tone Selection
                    Text(
                        text = stringResource(R.string.label_tone),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.tones.forEach { tone ->
                            FilterChip(
                                selected = uiState.tone == tone,
                                onClick = { viewModel.updateTone(tone) },
                                label = { Text(tone, fontSize = 12.sp) },
                                modifier = Modifier.testTag("tone_chip_$tone")
                            )
                        }
                    }

                    AnimatedVisibility(visible = showAdvancedSettings) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.label_include_emojis),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = uiState.includeEmojis,
                                    onCheckedChange = { viewModel.updateIncludeEmojis(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = LinkedInBlue)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.label_target_hashtags, uiState.hashtagCount),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = uiState.hashtagCount.toFloat(),
                                onValueChange = { viewModel.updateHashtagCount(it.toInt()) },
                                valueRange = 3f..10f,
                                steps = 7,
                                colors = SliderDefaults.colors(thumbColor = LinkedInBlue, activeTrackColor = LinkedInBlue)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.customInstruction,
                                onValueChange = { viewModel.updateCustomInstruction(it) },
                                label = { Text(stringResource(R.string.label_custom_prompt_instruction)) },
                                placeholder = { Text(stringResource(R.string.placeholder_custom_instruction)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateDraft() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("generate_post_btn"),
                            enabled = !uiState.isGeneratingPost,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LinkedInBlue)
                        ) {
                            if (uiState.isGeneratingPost) {
                                val loadingLabel = stringResource(R.string.loading_content)
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .semantics { contentDescription = loadingLabel },
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = stringResource(R.string.cd_generate_post_icon),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.btn_generate_draft))
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.generateVariations() },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isGeneratingPost
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.cd_variations),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.btn_ab_variations))
                        }
                    }
                }
            }

            // Post Variations Chips (if generated)
            if (uiState.variations.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.title_ab_variations_generated),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.variations.forEachIndexed { index, variation ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.updatePostText(variation) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = stringResource(R.string.label_variation, index + 1),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = LinkedInBlue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = variation,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }


            // Quality Meter Component
            QualityMeter(result = uiState.validationResult)

            // Rich Text Editor Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_draft_content),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val copyToastMsg = stringResource(R.string.msg_copied_clipboard)
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.postText))
                            Toast.makeText(context, copyToastMsg, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.cd_copy_post),
                                tint = LinkedInBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.postText,
                        onValueChange = { viewModel.updatePostText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("post_text_editor"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LinkedInBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Image Generation Banner Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = stringResource(R.string.cd_image),
                                tint = LinkedInBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.label_attached_image),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.generateNewAiImage() },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isGeneratingImage
                        ) {
                            if (uiState.isGeneratingImage) {
                                val loadingLabel = stringResource(R.string.loading_content)
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .semantics { contentDescription = loadingLabel },
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.btn_new_image),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.btn_new_image), fontSize = 12.sp)
                            }
                        }
                    }
                }

            }

            // Mock LinkedIn Post Preview Card
            PostPreviewCard(
                content = uiState.postText,
                imageUrl = uiState.imageUrl
            )

            // Primary Bottom Action Bar (Save Draft, Publish / Share to LinkedIn)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.saveDraft(PostStatus.DRAFT) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_draft_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = stringResource(R.string.cd_save_draft),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_save_draft))
                }

                Button(
                    onClick = {
                        viewModel.saveDraft(PostStatus.PUBLISHED)
                        // Launch native share intent for LinkedIn / apps with image support
                        scope.launch {
                            SharingUtils.sharePost(context, uiState.postText, uiState.imageUrl)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("publish_post_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LinkedInBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(R.string.cd_publish),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_publish_post))
                }
            }
        }
    }

    // Image Selection Bottom Sheet
    if (showImagePicker) {
        ModalBottomSheet(
            onDismissRequest = { 
                showImagePicker = false
                viewModel.closeImagePicker()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_choose_visual_style),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.subtitle_choose_visual_style),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )
                
                if (uiState.isLoadingImages) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val loadingLabel = stringResource(R.string.loading_content)
                        CircularProgressIndicator(
                            modifier = Modifier.semantics { contentDescription = loadingLabel },
                            color = LinkedInBlue
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.height(350.dp)
                    ) {
                        items(uiState.imageOptions) { url ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (uiState.imageUrl == url) 3.dp else 0.dp,
                                        color = if (uiState.imageUrl == url) LinkedInBlue else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.selectImage(url)
                                        showImagePicker = false
                                    }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = stringResource(R.string.cd_ai_image_option),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                if (uiState.imageUrl == url) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(LinkedInBlue.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        Surface(
                                            color = LinkedInBlue,
                                            shape = CircleShape,
                                            modifier = Modifier.padding(6.dp).size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { 
                        showImagePicker = false
                        viewModel.closeImagePicker()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        }
    }
}

