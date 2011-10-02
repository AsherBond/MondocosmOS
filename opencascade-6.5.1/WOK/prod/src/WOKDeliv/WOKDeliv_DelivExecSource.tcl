proc WOKDeliv_DelivExecSource:Process {unitname destination} {
    upack -c $unitname -o $destination -t source
    return $destination.Z
}

