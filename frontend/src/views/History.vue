<template>
  <div class="history">
    <h2>Operation History</h2>
    <p class="text-muted">View your previous steganography operations</p>
    
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading history...</p>
    </div>
    
    <div v-else-if="history.length === 0" class="alert alert-info" role="alert">
      No history found. Try encoding some messages first!
    </div>
    
    <div v-else>
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>File Name</th>
              <th>Message</th>
              <th>Created At</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in history" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.fileName }}</td>
              <td>
                <button 
                  @click="toggleMessage(item.id)" 
                  class="btn btn-sm btn-outline-primary"
                >
                  {{ expandedMessages.includes(item.id) ? 'Hide Message' : 'Show Message' }}
                </button>
                <div v-if="expandedMessages.includes(item.id)" class="mt-2 p-2 bg-light border rounded">
                  <p style="white-space: pre-wrap;">{{ item.message }}</p>
                </div>
              </td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td>
                <a :href="'http://localhost:8080/api/steganography/download/' + item.id" 
                   class="btn btn-sm btn-success" 
                   download
                >
                  Download
                </a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'History',
  data() {
    return {
      history: [],
      loading: true,
      expandedMessages: []
    };
  },
  mounted() {
    this.fetchHistory();
  },
  methods: {
    fetchHistory() {
      this.loading = true;
      
      axios.get('http://localhost:8080/api/steganography/history')
        .then(response => {
          this.history = response.data;
          this.loading = false;
        })
        .catch(error => {
          console.error('Error fetching history:', error);
          this.loading = false;
          alert('Failed to load history. Please try again later.');
        });
    },
    
    toggleMessage(id) {
      if (this.expandedMessages.includes(id)) {
        this.expandedMessages = this.expandedMessages.filter(item => item !== id);
      } else {
        this.expandedMessages.push(id);
      }
    },
    
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();
    }
  }
};
</script>