#README

Why Android navigation and Why Single Activity
New convention pushed by Google.
It gives an easier, cleaner, and safer way to go around navigation and app structure.
Single App activity become difficult for a bigger app but a small number of activity should be created.

Why Retrofit
Most popular way and fastest way to implement REST API communication
Retrofit could be use with Rx or Coroutine
I haven't use the latest version to be able to target a lower version of Android.

Why Glide:
Glide, Picasso, ImageLoader are really similar and used heavily.
Glide seems to have a faster response time and better cache management.
Glide library size though is bigger than Picasso.
Fresco is the new comer and seems to offer better memory management and more functionality but is less easy to use.

Why Room
Room is the go to database/persistent storage. The setup and usage of Room is really simple and convenient

#Code Coverage result
component	0% (0/2)	0% (0/7)	0% (0/17)
database	0% (0/10)	0% (0/42)	0% (0/217)
interactor	0% (0/3)	0% (0/7)	0% (0/27)
model	75% (6/8)	77% (14/18)	77% (14/18)
network	66% (4/6)	61% (11/18)	27% (11/40)
repository	100% (12/12)	100% (19/19)	93% (62/66)
ui	23% (9/38)	20% (16/78)	21% (49/232)
utils	25% (1/4)	57% (4/7)	41% (7/17)
BuildConfig	0% (0/1)	0% (0/1)	0% (0/1)
MainActivity	0% (0/3)	0% (0/5)	0% (0/19)



#Improvments

Use Dependency injection for ApiHelper, Repositories, and DAO

Add UI tests and Integration tests

Add Database tests

