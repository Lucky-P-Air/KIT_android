# KIT_android
Android App for KIT (Keep In Touch) by Nepenthe
<p align="left">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=kotlin,androidstudio" />
  </a>
</p>

## AndroidStudio recommended for development & easy integration of dependencies

### Local Development
To try it out:

1. Clone/Download this repo
2. Launch AndroidStudio & open the project via the repo's parent directory
3. Assuming you're one of the current developers/users of the web application, you'll need to make your own Secrets.kt file
   - Create a Secrets class in the `/app/src/main/java/com/example/kit/network/` package
   - The Secrets class shall contain one value, `headers`

       `val headers = mutableMapOf<String, String>(`    
       `    "Accept" to "application/vnd.api+json",`    
       `    "Authorization" to "Token your_personal_token_would_go_here",`    
       `    "Content-Type" to "application/vnd.api+json")`    
                
                
5. In AndroidStudio, use the Device Manager to setup an Android phone/device emulator
6. Run the app on the newly configured device emulator. 

