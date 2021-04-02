/* eslint-disable @typescript-eslint/no-unused-vars */
import { WebPlugin } from '@capacitor/core';

import type { FirebaseStoragePlugin, UploadFileOptions, UploadFileResults } from './definitions';

export class FirebaseStorageWeb
  extends WebPlugin
  implements FirebaseStoragePlugin {
  watchFileCreation (_options: { path: string; }): Promise<{ url: string; }> {
    throw new Error('Method not implemented.');
  }

  deleteFile (_options: { path: string; }): Promise<{ message: string; }> {
    throw new Error('Method not implemented.');
  }
    
  async uploadFile(_options: UploadFileOptions): Promise<UploadFileResults> {
    throw new Error('Method not implemented.');
  }

  async getDownloadUrl(_options: { path: string }): Promise<{ url: string}> {
    throw new Error('Method not implemented.');
  }
}
