from distutils.core import setup, Extension

module1 = Extension('parse_layer',
                    include_dirs = ['/usr/include/python2.7/'],
                    libraries = ['parse', 'python2.7', 'pthread'],
                    library_dirs = ['/usr/lib'],
                    sources = ['parse_layer.c'])

setup (name = 'parse_layer',
       version = '1.0',
       ext_modules = [module1])