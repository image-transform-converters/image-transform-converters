[![](https://travis-ci.com/image-transform-converters/image-transform-converters.svg?branch=master)](https://travis-ci.com/image-transform-converters/image-transform-converters)

# image-transform-converters
Conversion between spatial transformations of various formats / parametrizations

### BdvAffineTransform 

aka: SourceTransform

This affine transformation transforms a voxel grid space, typically of a stored image, to a physical space:

```                           
3D_physical_space = BdvAffineTransform( 3D_voxel_grid )
```

You find this transformation usually find in the XML files that are associated with HDF5 Bdv images. In the XML files it is given in a row-packed 4D matrix form:  
```
<affine>-0.0054 0.0062 -0.0058 191.5115 -0.0046 0.0035 0.0081 42.2484 0.0070 0.0070 0.0010 105.6954 </affine>
```


### BdvViewerTransform 

aka: BdvViewerAffineTransform, ViewerTransform

This affine transformation transforms from a physical space to the pixel grid of the BigDataViewer window on your computer monitor :
```
3D_pixel_grid_bdv_window = BdvAffineTransform( 3D_physical_space )
```
It may be confusing that the output is 3D as you monitor is 2D only. The convention is that pixels that have a z-coordinate of zero (in the `3D_pixel_grid_bdv_window`) are in the plane of your computer screen, and negative values are behind your screen while positive values are in front of your screen (i.e. closer to you). Typically only the pixels with z = 0 are drawn into the BDV window. Another convention is that the coordinate `(0,0,0)` corresponds to the upper left corner of the BDV window.

You can obtain this transformation, e.g., in the MoBIE Fiji plugin by a right click into the BDV window and selecting `Log current location`. The output will look like this:
```$xslt
Position:
(139.32571666666666,106.03463333333332,142.70000000000002)
View:
1.8521376755672165, 0.0, 0.0, 64.94959098626336, 0.0, 1.8521376755672165, 0.0, 69.60926068837793, 0.0, 0.0, 1.8521376755672165, -264.30004630344183
Normalised view:
n0.002893965118073776,n0.0,n0.0,n-0.40320376408396347,n0.0,n0.002893965118073776,n0.0,n-0.3068605301744095,n0.0,n0.0,n0.002893965118073776,n-0.4129688223491279
```
`Position` is computed as:
```$xslt
3D_physical_space = BdvAffineTransform.INVERSE( 3D_pixel_grid_bdv_window )
```
`View` is the ViewerTransform.

`Normalised view` is not discussed here as it is a MoBIE feature.
 
### BigDataViewer: from canvas to voxel in image source

If you have the coordinate of a pixel in the BDV window (`canvasPosition`), you can find the corresponding voxel in an image source like this (imglib2 code):

```$xslt
AffineTransform3D viewerToSourceTransform = new AffineTransform3D();
viewerToSourceTransform.preConcatenate( viewerTransform.inverse() );
viewerToSourceTransform.preConcatenate( sourceTransform.inverse() );
viewerToSourceTransform.apply( canvasPosition, sourceVoxelGridPosition );
```

