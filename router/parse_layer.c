#include <Python.h>
#include <parse.h>
#include <pthread.h>

#define PARSE_APPLICATION_ID	""
#define PARSE_CLIENT_KEY		""
#define PARSE_INSTALLATION_ID	""

static PyObject *callback = NULL;
static ParseClient client;
static int skipped_first_notification = 0;

static void * push_loop(void *arg) {
	parseRunPushLoop(*(ParseClient*)(arg));
	return NULL;
}

static void push_callback(ParseClient client, int error, const char *buffer) {

	if (!skipped_first_notification) {
		skipped_first_notification = 1;
		return;
	}
	else if (error == 0 && buffer != NULL && callback != NULL) {
		PyGILState_STATE state = PyGILState_Ensure();
		PyObject *arglist = Py_BuildValue("(s)", buffer);
		PyObject *result = PyObject_CallObject(callback, arglist);
		Py_DECREF(arglist);
		if(result != NULL) {
			Py_DECREF(result);
		}
		PyGILState_Release(state);
	}
} 

static PyObject * start_parse(PyObject *self, PyObject *args) {
	PyObject *result = NULL;
    PyObject *temp;
    pthread_t push_thread;

    if (PyArg_ParseTuple(args, "O:start_parse", &temp)) {
        if (!PyCallable_Check(temp)) {
            PyErr_SetString(PyExc_TypeError, "parameter must be callable");
            return NULL;
        }
        Py_XINCREF(temp);
        Py_XDECREF(callback);
        callback = temp;
        
        client = parseInitialize(PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		parseSetInstallationId(client, PARSE_INSTALLATION_ID);

		parseSetPushCallback(client, push_callback);
		parseStartPushService(client);

		pthread_create(&push_thread, NULL, push_loop, &client);

        Py_INCREF(Py_None);
        result = Py_None;
    }
    return result;
}

static PyObject * update_spot(PyObject *self, PyObject *args) {
	PyObject *result = NULL;
	int isFree;
	char data[100], *parkingId, *spotId;

	if (PyArg_ParseTuple(args, "ssi", &parkingId, &spotId, &isFree)) {
		sprintf(data, "{ \"parkingId\": \"%s\", \"spotId\": \"%s\", \"isFree\": \"%s\" }",
				parkingId, spotId, isFree ? "wolne" : "zajete"); 
		parseSendRequest(client, "POST", "/1/functions/update_spot", data, NULL);
	}

	Py_INCREF(Py_None);
    result = Py_None;
    return result;
}

static PyObject * register_device(PyObject *self, PyObject *args) {
	PyObject *result = NULL;
	char reg_data[100], *channelId;

	if(PyArg_ParseTuple(args, "s", &channelId)) {
		sprintf(reg_data, "{ \"channelId\": \"%s\", \"installationId\": \"%s\" }", channelId, PARSE_INSTALLATION_ID);
		parseSendRequest(client, "POST", "/1/functions/register_device", reg_data, NULL);
	}

	Py_INCREF(Py_None);
    result = Py_None;
    return result;
}

static PyMethodDef parse_methods[] = {
    {"update_spot",  update_spot, METH_VARARGS, "Send update to parse cloud"},
    {"start_parse", start_parse, METH_VARARGS, "Start parse passing a callback function"},
    {"register_device", register_device, METH_VARARGS, "Register device in a push channel"},
    {NULL, NULL, 0, NULL}
};

PyMODINIT_FUNC initparse_layer(void) {
	PyEval_InitThreads();
	(void) Py_InitModule("parse_layer", parse_methods);
}


