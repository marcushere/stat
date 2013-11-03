#!/usr/bin/perl
use strict;
use warnings;
use HTML::TreeBuilder;
use WWW::Mechanize;

# start out on a random page
my $nexturl = "http://en.wikipedia.org/wiki/Provinces_of_Italy"
#"http://en.wikipedia.org/wiki/Special:Random";
my $mech = WWW::Mechanize->new();
$mech->agent_alias('Windows Mozilla');
$mech->get($nexturl);
# find the url of the random wikipedia page
$nexturl = $mech->uri();

#initialize tree root object
my $root = HTML::TreeBuilder->new();

# director boolean to fetch the next page or not
my $nextpage = 1;

do {
    if ($nextpage) {
	# make a new tree object, open up and parse the file at the url
	$root = HTML::TreeBuilder->new_from_url($nexturl);
	
	# turn the root object into and HTML::Element object
	$root->elementify();
    }
    
    # get the first (or next) <p> element
    my $p = $root->look_down(_tag=>"p");
    # get its child elements
    my @pcont = $p->content_list();
    # make a tag variable for the result
    my $result = -1;
    # loop through the children, looking for the first <a>
    for my $i (0..$p->content_list()) {
	# only look at children that are tags (not text)
	if (ref $pcont[$i]) {
	    # get the tag name, only look at it if it's an <a>
	    my $tag = $pcont[$i]->tag();
	    if ($tag eq "a"){
		# for links, the first content will be the linktext
		my @tagcont = $pcont[$i]->content_list();
		# get the href and title
		my %attr = $pcont[$i]->all_attr();
		my $href = $attr{href};
		my $title = $attr{title};
		# retrieve the source for the paragraph
		my $src = $p->as_HTML();
		# make sure an open ( before the tag occurs
		if (index($src,"(")<index($src,$href) && index($src,"(")>0){
		    # make sure there is also a close ) before the tag
		    if (index($src,")")<index($src,$href) && index($src,"(")>0){
			$result = $i;
			#print "href: $href,\n title: $title\n";
			last;
		    }
		} else {
		    # if there's no () expression, take the tag
		    $result = $i;
		    #print "href: $href,\n title: $title\n";
		    last;
		}
	    }
	}
    }
    # if there was a positive result, finish up
    if ($result > -1) {
	# set the key to the current url
	my $key = $nexturl;
	# retrieve attribute object
	my %attr = $pcont[$result]->all_attr();
	# get the next url
	$nexturl = "http://en.wikipedia.org$attr{href}";
	# set the value to the next url
	my $val = $nexturl;
	# set flag to move on the the next page
	$nextpage = 1;
	# print out result
	print "kpey $key\n  ->$val\n\n";
    } else {
	# set flag to try the next <p>
	$nextpage = 0;
	print "(next <p>)";
    }
    
    # reset result marker
    $result = -1;
    
    
    
    
} while (not($nexturl eq "http://en.wikipedia.org/wiki/Special:Random"));
