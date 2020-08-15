Music Player - A Music Player that lists and plays the audio files on the device

This app is a ViewPager with three fragments.  

The first fragment lists all of the audio files on the device and plays them.
This fragment uses the paging library to display a paged list of livedata that is observed

The second and third fragments are for artists and albums, respectively.
They list all artists and albums on the device, clicking one displays a new fragment with a list of songs for the artist/album.
These fragment observe livedata to populate the lists


This app is a work in progress, I intend to continue to add to and improve it.
I may also put up a kotlin version in the future.
