#!/urs/bin/python

import sys
import numpy as np
from numpy import float32, int32, uint8, dtype, genfromtxt

N = len( sys.argv )

x = genfromtxt( sys.argv[ 1 ] )
y = genfromtxt( sys.argv[ 2 ] )
d = y - x

if len(d.shape) == 1:
    dist=np.linalg.norm( d )
    print( "%f" % ( dist ))
else:
    dist=np.linalg.norm( d, axis=1 )
    for d in dist:
        print( "%f" % ( d ))
