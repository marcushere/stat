#!/usr/bin/perl
use strict;
use warnings;
use WWW::Mechanize;

my $pageurl = "";

# var to store the next url to try
my $nexturl =# "http://en.wikipedia.org/wiki/San_Severino";
#"http://en.wikipedia.org/wiki/Physics";
#"http://en.wikipedia.org/wiki/Body_of_water";
#"http://en.wikipedia.org/wiki/Perth,_Western_Australia";
"http://en.wikipedia.org/wiki/Special:Random";
#$mech->get( "http://en.wikipedia.org/wiki/Special:Random");

# loop through the pages
do {
#create mech object I think?
    my $mech = WWW::Mechanize->new();

# make the client look like Windows Mozilla
# Wikipedia returns 403 error otherwise
    $mech->agent_alias('Windows Mozilla');

#for my $i (1,2){
    $mech->get($nexturl);
    
    # get the html content of the page
    my $htext = $mech->content();
    
    # replace everything up through the first <p> tag with <p>
    $htext =~ s/(?ms)(.)+?<p>/<p>/;
    # matching the content inside the <p> tag
    # not sure what this was for....
    $htext =~ s/\<\/p\>(?ms)(.)++/<\/p>/;

    # resetting var $nexturl
    $nexturl = "";
    my $nexttitle = "";
    # it does the work for me getting the title
    my $pagename = $mech->title();
    # get the actual url (for when it goes to the "random page" on wikipedia
    $pageurl = $mech->uri();
    # remove the " - Wikipedia..." from the page title
    $pagename = substr($pagename,0,-34);
    
    # I want to be able to use $1 (the link content) after it will be changed
    my $fst = $1;
#    print "htext: $htext\n\n";
    # flag variable to tell loop when to exit
    my $finish = 0;
    do {
	# match the link url ($2), title ($3), and text ($4)
	my @arr1 = $htext =~ /^(.*?)href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
#	print "arr1: @arr1";
	# make sure the link isn't for an IPA pronounciation guide
	if ($arr1[1] =~ /(.)*?(Help:IPA)+/) {
#	    print 1;
	    # match everything up through the start of the first <a> tag,
	    # replace it with 's'
	    $htext =~ s/^(.)+?\<a /s/;
	    # update $fst variable to match the content of the first link
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
        # make sure the link isn't a citation
	} elsif ($arr1[1] =~ /(.)+?(cite)+/) {
#	    print "2";#, htext: $htext\n\n\n";
	    $htext =~ s/^(.)+?\<a /s/;
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
	# make sure the link isn't to a help page, or media file
	} elsif ($arr1[1] =~ /(.)+?(File:|.ogg|wikimedia.org|Help:)+/) {
	    $htext =~ s/^(.)+?\<a /s/;
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
	# make sure link is not inside a parenthetical statement
	# (check for '(' before the first href
	} elsif ($htext =~ /^(.(?!href))+?\(+(.)*?href=\"/){
#	    print "3";#, htext: $htext\n\n\n";
	    # if there is a parenthetical, replace everything up 
	    # until the <a> tag after it is closed with 's'
	    $htext =~ s/^(.)+?\)+?(.)*?\<a /s/;
	# else, we finish
	} else {
	    $finish = 1;
	}
    } while ($finish==0);
#    print "htext: $htext\n\n\n";
    
    # match the string to extract the next url and next title
    $htext =~ /^.+?href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
    $nexturl = "http://en.wikipedia.org$1";
    $nexttitle = $3;

    # only print things out if the next url does not match the pattern
    # "http://en.wikipedia.org[whitespace]"
    if ($nexturl =~ /(http:\/\/en.wikipedia.org)(\S)+/) {
	print "url: $nexturl,\n linktitle: $nexttitle\n article title: $pagename\n article url: $pageurl\n\n";
    } else {
	# if the url is obviously wrong, just make the next page a random page
	$nexturl = "http://en.wikipedia.org/wiki/Special:Random";
    }
    
#quit when we reach the philosophy page
} until ($pageurl eq "http://en.wikipedia.org/wiki/Philosophy");
#print "Reached philosophy!!";
