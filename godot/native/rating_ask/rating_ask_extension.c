// GDExtension entry point for the iOS rating prompt (Godot 4.7, plain C).
//
// Registers one class, RatingAskIOS (extends Object, abstract), with one static
// method, request(), which hands over to rating_ask_request() in
// RatingAsk.swift (iOS) or a no-op stub (macOS editor). GDScript reaches it through
// ClassDB.class_call_static("RatingAskIOS", "request"); see
// res://scripts/rating_ask.gd, the only caller.
//
// gdextension_interface.h is not committed: build.sh dumps it from the Godot
// 4.7.2 binary the project uses, so the header always matches the engine.

#include "gdextension_interface.h"

#include <stddef.h>
#include <stdint.h>

#ifdef RATING_ASK_STUB
// macOS editor build: registers the same class so the editor and the headless
// tests load the extension cleanly, but asks nothing.
static void rating_ask_request(void) {
}
#else
extern void rating_ask_request(void); // RatingAsk.swift
#endif

// StringName is one pointer wide on 64-bit. Built "static" from string
// literals, so they are never destroyed (see string_name_new_with_latin1_chars).
typedef struct {
	_Alignas(8) uint8_t data[8];
} RatingStringName;

static RatingStringName class_name;
static RatingStringName parent_name;
static RatingStringName method_name;
static GDExtensionClassLibraryPtr library;
static GDExtensionInterfaceClassdbUnregisterExtensionClass unregister_class;

static void free_instance(void *p_class_userdata, GDExtensionClassInstancePtr p_instance) {
	(void)p_class_userdata;
	(void)p_instance; // abstract class: never instantiated
}

static void request_call(void *p_method_userdata, GDExtensionClassInstancePtr p_instance,
		const GDExtensionConstVariantPtr *p_args, GDExtensionInt p_argument_count,
		GDExtensionVariantPtr r_return, GDExtensionCallError *r_error) {
	(void)p_method_userdata;
	(void)p_instance;
	(void)p_args;
	(void)r_return; // void method: the engine leaves the return Variant nil
	if (p_argument_count != 0) {
		r_error->error = GDEXTENSION_CALL_ERROR_TOO_MANY_ARGUMENTS;
		r_error->expected = 0;
		return;
	}
	r_error->error = GDEXTENSION_CALL_OK;
	rating_ask_request();
}

static void request_ptrcall(void *p_method_userdata, GDExtensionClassInstancePtr p_instance,
		const GDExtensionConstTypePtr *p_args, GDExtensionTypePtr r_ret) {
	(void)p_method_userdata;
	(void)p_instance;
	(void)p_args;
	(void)r_ret;
	rating_ask_request();
}

// The entry point resolves every interface function up front and keeps the
// pointers for the init callbacks.
static GDExtensionInterfaceStringNameNewWithLatin1Chars string_name_new;
static GDExtensionInterfaceClassdbRegisterExtensionClass6 register_class;
static GDExtensionInterfaceClassdbRegisterExtensionClassMethod register_method;

static void register_types(void *p_userdata, GDExtensionInitializationLevel p_level) {
	(void)p_userdata;
	if (p_level != GDEXTENSION_INITIALIZATION_SCENE) {
		return;
	}
	string_name_new(&class_name, "RatingAskIOS", 1);
	string_name_new(&parent_name, "Object", 1);
	string_name_new(&method_name, "request", 1);

	GDExtensionClassCreationInfo6 info = { 0 };
	info.is_abstract = 1;
	info.is_exposed = 1;
	info.free_instance_func = free_instance;
	register_class(library, &class_name, &parent_name, &info);

	GDExtensionClassMethodInfo method = { 0 };
	method.name = &method_name;
	method.call_func = request_call;
	method.ptrcall_func = request_ptrcall;
	method.method_flags = GDEXTENSION_METHOD_FLAG_NORMAL | GDEXTENSION_METHOD_FLAG_STATIC;
	method.has_return_value = 0;
	method.return_value_metadata = GDEXTENSION_METHOD_ARGUMENT_METADATA_NONE;
	register_method(library, &class_name, &method);
}

static void unregister_types(void *p_userdata, GDExtensionInitializationLevel p_level) {
	(void)p_userdata;
	if (p_level != GDEXTENSION_INITIALIZATION_SCENE) {
		return;
	}
	unregister_class(library, &class_name);
}

// entry_symbol in res://addons/rating_ask/rating_ask.gdextension
__attribute__((visibility("default")))
GDExtensionBool rating_ask_init(GDExtensionInterfaceGetProcAddress p_get_proc_address,
		GDExtensionClassLibraryPtr p_library, GDExtensionInitialization *r_initialization) {
	library = p_library;
	string_name_new = (GDExtensionInterfaceStringNameNewWithLatin1Chars)p_get_proc_address("string_name_new_with_latin1_chars");
	register_class = (GDExtensionInterfaceClassdbRegisterExtensionClass6)p_get_proc_address("classdb_register_extension_class6");
	register_method = (GDExtensionInterfaceClassdbRegisterExtensionClassMethod)p_get_proc_address("classdb_register_extension_class_method");
	unregister_class = (GDExtensionInterfaceClassdbUnregisterExtensionClass)p_get_proc_address("classdb_unregister_extension_class");
	if (!string_name_new || !register_class || !register_method || !unregister_class) {
		return 0; // older engine than compatibility_minimum promised
	}
	r_initialization->minimum_initialization_level = GDEXTENSION_INITIALIZATION_SCENE;
	r_initialization->userdata = NULL;
	r_initialization->initialize = register_types;
	r_initialization->deinitialize = unregister_types;
	return 1;
}
