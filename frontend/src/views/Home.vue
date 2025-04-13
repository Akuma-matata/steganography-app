<template>
  <div class="home">
    <div class="jumbotron bg-light p-5 rounded">
      <h1 class="display-4">Steganography Service</h1>
      <p class="lead">
        Hide your secret messages within images using steganography techniques.
      </p>
      <hr class="my-4">
      <p>
        Steganography is the practice of concealing a message within another message or a physical object.
        In this case, we hide text messages inside digital images without noticeable changes to the image.
      </p>
      <div class="d-flex gap-2 mt-4">
        <router-link to="/encode" class="btn btn-primary btn-lg">
          Encode Message
        </router-link>
        <router-link to="/decode" class="btn btn-success btn-lg">
          Decode Message
        </router-link>
      </div>
    </div>
    
    <div class="row mt-5">
      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Encode Messages</h5>
            <p class="card-text">
              Hide your sensitive messages within an image that looks normal to others.
            </p>
            <router-link to="/encode" class="btn btn-sm btn-primary">Try Encoding</router-link>
          </div>
        </div>
      </div>
      
      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Decode Messages</h5>
            <p class="card-text">
              Extract hidden messages from images that were encoded using our service.
            </p>
            <router-link to="/decode" class="btn btn-sm btn-success">Try Decoding</router-link>
          </div>
        </div>
      </div>
      
      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">View History</h5>
            <p class="card-text">
              Browse your previously encoded images and the messages they contain.
            </p>
            <router-link to="/history" class="btn btn-sm btn-info">View History</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>