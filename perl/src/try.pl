#!/usr/bin/perl
use strict;
use warnings;
use WWW::Mechanize;

my $pageurl = "";

my $nexturl =# "http://en.wikipedia.org/wiki/San_Severino";
#"http://en.wikipedia.org/wiki/Physics";
#"http://en.wikipedia.org/wiki/Body_of_water";
#"http://en.wikipedia.org/wiki/Perth,_Western_Australia";
"http://en.wikipedia.org/wiki/Special:Random";
#$mech->get( "http://en.wikipedia.org/wiki/Special:Random");
do {
#create mech object I think?
my $mech = WWW::Mechanize->new();

$mech->agent_alias('Windows Mozilla');

#for my $i (1,2){
    $mech->get($nexturl);
    
    my $htext = $mech->content();
    $htext =~ s/(?ms)(.)+?<p>/<p>/;
    $htext =~ s/\<\/p\>(?ms)(.)++/<\/p>/;

    $nexturl = "";
    my $nexttitle = "";
    my $pagename = $mech->title();
    $pageurl = $mech->uri();
    $pagename = substr($pagename,0,-34);
    
    my $fst = $1;
#    print "htext: $htext\n\n";
    my $finish = 0;
    do {
	my @arr1 = $htext =~ /^(.*?)href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
#	print "arr1: @arr1";
	if ($arr1[1] =~ /(.)*?(Help:IPA)+/) {
#	    print 1;
	    $htext =~ s/^(.)+?\<a /s/;
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
	} elsif ($arr1[1] =~ /(.)+?(cite)+/) {
#	    print "2";#, htext: $htext\n\n\n";
	    $htext =~ s/^(.)+?\<a /s/;
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
	} elsif ($arr1[1] =~ /(.)+?(File:|.ogg|wikimedia.org|Help:)+/) {
	    $htext =~ s/^(.)+?\<a /s/;
	    my @arr = $htext =~ /<a href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
	    $fst = $arr[0];
	} elsif ($htext =~ /^(.(?!href))+?\(+(.)*?href=\"/){
#	    print "3";#, htext: $htext\n\n\n";
	    $htext =~ s/^(.)+?\)+?(.)*?\<a /s/;
	} else {
	    $finish = 1;
	}
    } while ($finish==0);
#    print "htext: $htext\n\n\n";
    
    $htext =~ /^.+?href=\"(.*?)\"(.*?)>(.+?)<\/a>/;
    $nexturl = "http://en.wikipedia.org$1";
    $nexttitle = $3;

    if ($nexturl =~ /(http:\/\/en.wikipedia.org)(\S)+/) {
	print "url: $nexturl,\n linktitle: $nexttitle\n article title: $pagename\n article url: $pageurl\n\n";
    } else {
	$nexturl = "http://en.wikipedia.org/wiki/Special:Random";
    }
    
} until ($pageurl eq "http://en.wikipedia.org/wiki/Philosophy");
#print "Reached philosophy!!";
