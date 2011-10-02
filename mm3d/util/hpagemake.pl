#!/usr/bin/perl -w

use HTML::Template;

foreach my $file ( @ARGV )
{
   if ( open( INFILE, $file ) )
   {
      my %vars = ();

      my @lt = localtime();

      $vars{ 'SECLEFT' }   = `cat secleft.htm`;
      $vars{ 'SECRIGHT' }  = `cat secright.htm`;
      $vars{ 'SECEND' }    = `cat secend.htm`;
      $vars{ 'TIMESTAMP' } = `date`;
      $vars{ 'DATE_YEAR' } = $lt[5] + 1900;

      while ( <INFILE> )
      {
         if ( /^(.*)=(.*)$/ )
         {
            my $key   = $1;
            my $value = $2;

            $key   =~ s/^\s+//g;
            $key   =~ s/\s+$//g;
            $value =~ s/^\s+//g;
            $value =~ s/\s+$//g;

            if ( $value =~ /^<(.*)$/ )
            {
               my $content_file = $1;
               my $text = `cat $content_file`;

               my %vars = ();

               $vars{ 'SECLEFT' }  = `cat secleft.htm`;
               $vars{ 'SECRIGHT' } = `cat secright.htm`;
               $vars{ 'SECEND' }   = `cat secend.htm`;
               $vars{ 'TIMESTAMP' } = `date`;
               $vars{ 'DATE_YEAR' } = $lt[5] + 1900;

               my $t = HTML::Template->new_array_ref( [ $text ], die_on_bad_params => 0 );
               $t->param( %vars );
               $value = $t->output;
            }

            $vars{ $key } = $value;
         }
      }
      close( INFILE );

      my $template = HTML::Template->new( 
         filename => "template.htm",
         die_on_bad_params => 0
      );

      $template->param( %vars );

      print $template->output;
   }
   else
   {
      die "$file: $!\n";
   }
}

