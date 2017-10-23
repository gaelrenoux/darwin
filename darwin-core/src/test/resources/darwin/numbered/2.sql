# --- !Up
This is an up

# !Define value
This is a define

#! !Down
This is a down with a ${value}

#! !Up Something else
This is another up with a ${value} and the same ${value}

#--- !Down #
This is another down

#!Define stuffAgain_2! and again
This is another define with ${value}

#--- !Up #
This is a last up with two variables: ${stuffAgain_2} and
${value}