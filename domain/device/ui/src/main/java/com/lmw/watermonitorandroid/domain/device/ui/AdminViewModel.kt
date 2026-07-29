package com.lmw.watermonitorandroid.domain.device.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmw.watermonitorandroid.domain.device.api.DeviceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val deviceService: DeviceService
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    init {
        browseFiles("/")
    }

    fun browseFiles(path: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            deviceService.browseFiles(path)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载失败"
                    )
                }
                .collect { response ->
                    _state.value = _state.value.copy(
                        files = response.files,
                        currentPath = response.path,
                        totalBytes = response.totalBytes,
                        usedBytes = response.usedBytes,
                        isLoading = false,
                        previewContent = null,
                        previewPath = null
                    )
                }
        }
    }

    fun navigateUp() {
        val current = _state.value.currentPath
        if (current == "/") return
        val parent = current.substringBeforeLast("/")
        browseFiles(if (parent.isEmpty()) "/" else parent)
    }

    fun readFile(path: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            deviceService.readFile(path)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "读取失败"
                    )
                }
                .collect { content ->
                    _state.value = _state.value.copy(
                        previewContent = content,
                        previewPath = path,
                        isLoading = false
                    )
                }
        }
    }

    fun closePreview() {
        _state.value = _state.value.copy(previewContent = null, previewPath = null)
    }

    fun requestDelete(path: String) {
        _state.value = _state.value.copy(deleteTarget = path)
    }

    fun cancelDelete() {
        _state.value = _state.value.copy(deleteTarget = null)
    }

    fun confirmDelete() {
        val target = _state.value.deleteTarget ?: return
        viewModelScope.launch {
            deviceService.deleteFile(target)
                .catch { e ->
                    _state.value = _state.value.copy(
                        deleteTarget = null,
                        errorMessage = e.message ?: "删除失败"
                    )
                }
                .collect { success ->
                    _state.value = _state.value.copy(deleteTarget = null)
                    if (success) {
                        _state.value = _state.value.copy(
                            successMessage = "删除成功"
                        )
                        browseFiles(_state.value.currentPath)
                    } else {
                        _state.value = _state.value.copy(
                            errorMessage = "删除失败"
                        )
                    }
                }
        }
    }

    fun uploadFile(name: String, bytes: ByteArray) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true, errorMessage = null)
            deviceService.uploadFile(name, bytes)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isUploading = false,
                        errorMessage = e.message ?: "上传失败"
                    )
                }
                .collect { success ->
                    _state.value = _state.value.copy(isUploading = false)
                    if (success) {
                        _state.value = _state.value.copy(
                            successMessage = "上传成功"
                        )
                        browseFiles(_state.value.currentPath)
                    } else {
                        _state.value = _state.value.copy(
                            errorMessage = "上传失败"
                        )
                    }
                }
        }
    }

    fun dismissMessage() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}