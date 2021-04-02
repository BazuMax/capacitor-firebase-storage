import type { ListenerCallback, PluginListenerHandle } from "@capacitor/core";

export interface UploadFileOptions {
  withImagePicker: boolean 
  title?: string,
  storagePath: string
}
export interface UploadFileResults {
  url: string
}
export interface FirebaseStoragePlugin {
  uploadFile(options: UploadFileOptions): Promise<UploadFileResults>;
  deleteFile(options: { path: string }): Promise<{ message: string }>;
  getDownloadUrl(options: { path: string }): Promise<{ url: string}>;
  watchFileCreation(options: { path: string }): Promise<{ url: string}>;

  addListener(eventName: string, listenerFunc: ListenerCallback): Promise<PluginListenerHandle> & PluginListenerHandle;
  removeAllListeners(): Promise<void>;
}
