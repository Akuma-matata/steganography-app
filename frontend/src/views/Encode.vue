<template>
  <div class="encode">
    <h2>Encode Message</h2>
    <p class="text-muted">Hide your secret message within an image</p>
    
    <div class="alert alert-info" role="alert">
      <strong>Note:</strong> For best results, we use PNG format for steganography. 
      If you upload a JPEG/JPG image, it will be automatically converted to PNG.
    </div>
    
    <div class="card mb-4">
      <div class="card-body">
        <form @submit.prevent="uploadFile">
          <div class="mb-3">
            <label for="image" class="form-label">Select an image:</label>
            <input type="file" class="form-control" id="image" accept="image/*" @change="onFileChange" required>
            <div v-if="fileWarning" :class="['form-text', isJpeg ? 'text-info' : 'text-danger']">
              {{ fileWarning }}
            </div>
            <div class="form-text">Supported formats: All image formats (JPEG/JPG will be converted to PNG)</div>
          </div>
          
          <div class="mb-3">
            <label for="message" class="form-label">Enter your secret message:</label>
            <textarea class="form-control" id="message" v-model="message" rows="4" required></textarea>
            <div class="form-text">The message will be hidden in the image using steganography.</div>
          </div>
          
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            {{ loading ? 'Encoding...' : 'Encode Message' }}
          </button>
        </form>
      </div>
    </div>
    
    <div v-if="preview" class="row">
      <div class="col-md-6">
        <div class="card mb-3">
          <div class="card-header">Original Image</div>
          <div class="card-body text-center">
            <img :src="preview" class="img-fluid mb-2" alt="Original image preview" style="max-height: 300px">
            <div v-if="isJpeg" class="mt-2">
              <small class="text-info">This JPEG image will be automatically converted to PNG for better steganography results.</small>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="encodedImageUrl" class="col-md-6">
        <div class="card mb-3">
          <div class="card-header">Encoded Image (PNG Format)</div>
          <div class="card-body text-center">
            <div class="alert alert-success">
              Message successfully hidden in the image!
            </div>
            <img :src="encodedImageUrl" class="img-fluid mb-2" alt="Encoded image" style="max-height: 300px">
            <div class="mt-3">
              <a :href="downloadUrl" class="btn btn-success" download>Download Encoded Image</a>
              <div class="mt-2 text-muted">
                <small>Important: Do not edit or re-save this image, especially as JPEG, as it will destroy the hidden message.</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Encode',
  data() {
    return {
      file: null,
      fileType: null,
      fileWarning: null,
      message: '',
      preview: null,
      encodedImageUrl: null,
      downloadUrl: null,
      loading: false,
      error: null,
      isJpeg: false
    };
  },
  methods: {
    onFileChange(e) {
      const file = e.target.files[0];
      if (!file) {
        this.preview = null;
        this.file = null;
        this.fileType = null;
        this.fileWarning = null;
        this.isJpeg = false;
        return;
      }
      
      this.file = file;
      this.fileType = file.name.split('.').pop().toLowerCase();
      
      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.preview = e.target.result;
      };
      reader.readAsDataURL(file);
      
      // Check if the file is JPEG/JPG
      if (this.fileType === 'jpg' || this.fileType === 'jpeg') {
        this.fileWarning = 'Note: This JPEG image will be automatically converted to PNG format for encoding.';
        this.isJpeg = true;
      } else if (this.fileType !== 'png' && this.fileType !== 'bmp') {
        this.fileWarning = `Note: ${this.fileType.toUpperCase()} will be processed, but PNG or BMP is recommended for best results.`;
        this.isJpeg = false;
      } else {
        this.fileWarning = null;
        this.isJpeg = false;
      }
    },
    
    uploadFile() {
      if (!this.file || !this.message) {
        return;
      }
      
      this.loading = true;
      this.error = null;
      this.encodedImageUrl = null;
      
      const formData = new FormData();
      formData.append('file', this.file);
      formData.append('message', this.message);
      
      axios.post('http://localhost:8080/api/steganography/encode', formData)
        .then(response => {
          this.downloadUrl = response.data.downloadUrl;
          // Load the encoded image
          axios.get(this.downloadUrl, { responseType: 'blob' })
            .then(imageResponse => {
              this.encodedImageUrl = URL.createObjectURL(imageResponse.data);
              this.loading = false;
            })
            .catch(error => {
              console.error('Error loading encoded image:', error);
              this.loading = false;
            });
        })
        .catch(error => {
          this.error = error.response ? error.response.data.error : 'An error occurred during encoding';
          this.loading = false;
          alert(this.error);
        });
    }
  }
};
</script>