
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <HttpClient.h>

#define FIREBASE_HOST "tracker-ca881.firebaseio.com"
#define FIREBASE_AUTH "L2vHYLgoIxux3BuiNxxtPD7lORhDJWk88P2WlADE"
#define WIFI_SSID "OnePlus 5T"
#define WIFI_PASSWORD "aaaaaaaaa"

const char* host = "maker.ifttt.com";
const char* apiKey = "c2eRchfs0NnHupPlRWXOPL";

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 19800,60000);
String date_time;

void setup() {
  
     Serial.begin(9600);
  
      timeClient.begin();
     
      WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
      Serial.print("connecting");
      
      while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
      }
      
      Serial.println();
      Serial.print("connected: ");
      Serial.println(WiFi.localIP());
      
      Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}



void loop() {
      FirebaseObject obj=Firebase.get("/");
      String expi=obj.getString("Container1/Expiry/");
      int threshold=obj.getInt("Container1/Threshold/");
     // Serial.println(threshold);
     // Serial.println(expi);
      int adcvalue=analogRead(A0); 
    
      String urlexpiry = "/trigger/containerexpiry/with/key/";
      String urlcontainer = "/trigger/emptycontainer/with/key/";
      urlcontainer += apiKey;
      urlexpiry += apiKey;
      Serial.print("connecting to ");
      Serial.println(host);
      WiFiClient client1,client2;
      const int httpPort = 80;
      if (!client1.connect(host, httpPort))
      {
            Serial.println("connection failed");
            return;
      }
      else
            Serial.println("connection to host");
      if (!client2.connect(host, httpPort))
      {
            Serial.println("connection failed");
            return;
      }
      else
            Serial.println("connection to host");      
      String weight;
      timeClient.update();
      
      
//      Serial.print("Requesting URL: ");
//      Serial.println(urlexpiry );
// 

      if(adcvalue<10)
        adcvalue = 0;
      else if(adcvalue>10 && adcvalue<150)
        adcvalue = 60;
      else if(adcvalue>150 && adcvalue<330)
        adcvalue = 130;
      else if(adcvalue>330 && adcvalue<390)
        adcvalue = 190;
      else if(adcvalue>390 && adcvalue<450)
        adcvalue = 250;
      else if(adcvalue>450 && adcvalue<470)
        adcvalue = 290;
      else if(adcvalue>470 && adcvalue<500)
        adcvalue= 340;
      else if(adcvalue>500 )
        adcvalue= 500;
      weight = String(adcvalue);
      Serial.println(adcvalue);
      date_time = timeClient.getFormattedDate();
      int index_date = date_time.indexOf("T");
      String date = date_time.substring(0, index_date);
      //Serial.println(date);
      Firebase.setString("Container1/Date/"+date, weight);
     
      if (Firebase.failed()) {
          Serial.print("pushing /logs failed:");
          Serial.println(Firebase.error());  
          return;
      }
     
        if(adcvalue<=threshold)
        {
            Serial.print("Requesting URL: ");
            Serial.println(urlcontainer);
            client1.print(String("POST ") + urlcontainer + " HTTP/1.1\r\n" +
                       "Host: " + host + "\r\n" + 
                       "Content-Type: application/x-www-form-urlencoded\r\n" + 
                       "Content-Length: 13\r\n\r\n" +
                       "value1=" + "refill the container "+ "\r\n");
        }
       
        
        if(expi.equals(date))
        {  
            client2.print(String("POST ") + urlexpiry + " HTTP/1.1\r\n" +
                       "Host: " + host + "\r\n" + 
                       "Content-Type: application/x-www-form-urlencoded\r\n" + 
                       "Content-Length: 13\r\n\r\n" +
                       "value1=" + "near expiry"+ "\r\n");
        }
      
    
      delay(2000);
}
