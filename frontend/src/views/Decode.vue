<template>
  <div class="decode">
    <h2>Decode Message</h2>
    <p class="text-muted">Extract a hidden message from an image</p>
    
    <div class="alert alert-info" role="alert">
      <strong>Note:</strong> Upload an image that has a hidden message encoded with our service.
      <br>
      <strong>Important:</strong> If the image was saved as JPEG/JPG after encoding, the hidden message may be corrupted or lost.
    </div>
    
    <div class="card mb-4">
      <div class="card-body">
        <form @submit.prevent="uploadFile">
          <div class="mb-3">
            <label for="image" class="form-label">Select an encoded image:</label>
            <input type="file" class="form-control" id="image" accept="image/*" @change="onFileChange" required>
            <div v-if="fileWarning" class="form-text text-danger">
              {{ fileWarning }}
            </div>
            <div class="form-text">Upload an image that has a hidden message</div>
          </div>
          
          <button type="submit" class="btn btn-success" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            {{ loading ? 'Decoding...' : 'Decode Message' }}
          </button>
        </form>
      </div>
    </div>
    
    <div v-if="preview" class="row">
      <div class="col-md-6">
        <div class="card mb-3">
          <div class="card-header">Encoded Image</div>
          <div class="card-body text-center">
            <img :src="preview" class="img-fluid mb-2" alt="Encoded image preview" style="max-height: 300px">
          </div>
        </div>
      </div>
      
      <div v-if="decodedMessage" class="col-md-6">
        <div class="card mb-3">
          <div class="card-header">Decoded Message</div>
          <div class="card-body">
            <div class="alert alert-success">
              Message successfully extracted!
            </div>
            <div class="mb-3">
              <label class="form-label">The hidden message is:</label>
              <div class="p-3 bg-light border rounded">
                <p style="white-space: pre-wrap;">{{ decodedMessage }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="error" class="col-md-6">
        <div class="card mb-3">
          <div class="card-header text-white bg-danger">Error</div>
          <div class="card-body">
            <div class="alert alert-danger">
              {{ error }}
            </div>
            <div v-if="fileType === 'jpg' || fileType === 'jpeg'" class="alert alert-warning">
              <strong>JPEG Format Detected:</strong> JPEG/JPG files use lossy compression which likely destroyed the hidden message.
              Always use PNG or BMP format when working with steganography.
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
  name: 'Decode',
  data() {
    return {
      file: null,
      fileType: null,
      fileWarning: null,
      preview: null,
      decodedMessage: null,
      loading: false,
      error: null
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
      
      // Reset decoded message and error
      this.decodedMessage = null;
      this.error = null;
      
      // Check if the file is JPEG/JPG
      if (this.fileType === 'jpg' || this.fileType === 'jpeg') {
        this.fileWarning = 'Warning: JPEG/JPG files use lossy compression which may have destroyed the hidden message. Results may be unreliable.';
      } else if (this.fileType !== 'png' && this.fileType !== 'bmp') {
        this.fileWarning = `Note: ${this.fileType.toUpperCase()} might not be an ideal format for steganography.`;
      } else {
        this.fileWarning = null;
      }
    },
    
    uploadFile() {
      if (!this.file) {
        return;
      }
      
      this.loading = true;
      this.error = null;
      this.decodedMessage = null;
      
      const formData = new FormData();
      formData.append('file', this.file);
      
      axios.post('http://localhost:8080/api/steganography/decode', formData)
        .then(response => {
          this.decodedMessage = response.data.message;
          this.loading = false;
        })
        .catch(error => {
          this.error = error.response ? error.response.data.error : 'An error occurred during decoding';
          this.loading = false;
        });
    }
  }
};
</script>